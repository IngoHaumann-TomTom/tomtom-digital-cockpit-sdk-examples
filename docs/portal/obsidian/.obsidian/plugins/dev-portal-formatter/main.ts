import {
	debounce,
	Editor,
	Notice,
	Plugin,
	stringifyYaml,
	TAbstractFile,
	TFile,
	TFolder,
	parseYaml,
} from "obsidian";
import * as prettier from "prettier";
import markdown from "prettier/parser-markdown";
import babel from "prettier/parser-babel";
import html from "prettier/parser-html";

const prettierOptions: prettier.Options = {
	semi: false,
	parser: "markdown",
	plugins: [markdown, babel, html],
	proseWrap: "always",
	htmlWhitespaceSensitivity: "ignore",
	printWidth: 100,
};

type NavigationEntry = {
	fileId?: string;
	title: string;
	items?: NavigationEntry[];
};

export default class DevPortalFormatter extends Plugin {
	async onload() {
		this.registerEvent(
			this.app.workspace.on("editor-change", (e, b) => {
				this.formatLinks(e);
				this.formatImages(e);
				this.formatFile(e);
			})
		);

		this.registerEvent(this.app.vault.on("rename", this.onRename));

		this.registerEvent(
			this.app.vault.on("modify", async (file) => {
				// Strips .md extension from automatically updated links
				const content = await this.app.vault.read(file as TFile);
				if (content.includes(".md")) {
					console.log(`Updating links in ${file.name}`);
					const mdLinks = Array.from(
						content.matchAll(/\(.+\.md.{0,}\)/g)
					).map((m) => m[0]);

					let updated = content;

					mdLinks.forEach((old) => {
						const newLink = old
							.replace(".md", "")
							.replace("(", "(/");
						updated = updated.replace(old, newLink);
					});

					this.app.vault.modify(file as TFile, updated);
				}
			})
		);
	}

	onRename = async (file: TAbstractFile, old: string) => {
		// Only check for markdown files
		if (!file.path.match(/\.md$/)) {
			return;
		}

		// Update file name to be kebab case.
		if (!file.path.match(/^[\/a-z\-]+\.md$/)) {
			new Notice(
				"File names should be lowercase and use - in stead of spaces (e.g. test-file-name)."
			);
			this.app.fileManager.renameFile(
				file,
				file.path.toLowerCase().replace(/[ ]/g, "-")
			);
		}

		// Find the nearest navigation file
		const navigation = this.lookForNavigation(file.parent);

		if (navigation) {
			const navigationPath = navigation.parent.path;
			const oldPath = old.replace(".md", "").replace(navigationPath, "");
			const navigationString = await this.app.vault.read(
				navigation as TFile
			);

			const navContent = parseYaml(navigationString);

			let existing: NavigationEntry;
			let parent: NavigationEntry;

			const lookForExistingOrParent = (entry: NavigationEntry) => {
				if (existing) return;

				if (entry.fileId == oldPath) {
					existing = entry;
					return;
				}

				if (
					!entry.fileId &&
					entry.title == this.toSentence(file.parent.name)
				) {
					parent = entry;
				}

				if (entry.items) {
					for (const item of entry.items) {
						lookForExistingOrParent(item);
					}
				}
			};

			navContent.menu.forEach((entry: NavigationEntry) => {
				lookForExistingOrParent(entry);
			});

			const newPath = file.path
				.replace(navigationPath, "")
				.replace(".md", "");

			const frontmatter = this.app.metadataCache.getCache(
				file.path
			)?.frontmatter;

			const newTitle =
				frontmatter?.title ||
				this.toSentence(file.name.replace(".md", ""));

			if (existing) {
				if (existing.title == newTitle && existing.fileId == newPath) {
					return;
				}
				// Update existing entry
				existing.title = newTitle;
				existing.fileId = newPath;
			} else if (parent) {
				// Add to existing parent
				if (!parent.items) {
					parent.items = [];
				}
				parent.items!.push({
					title: newTitle,
					fileId: newPath,
				});
			} else {
				// Add parents and entry
				const parents = newPath.split("/");
				parents.pop();
				parents.shift();

				const findOrCreateParent = (
					level: NavigationEntry[],
					toCreate: string[]
				) => {
					if (!toCreate.length) {
						// No more parents to create, add entry
						level.push({
							title: newTitle,
							fileId: newPath,
						});
						return;
					}

					const existingParent = level.find(
						(entry) =>
							entry.title.toLowerCase() ==
							this.toSentence(toCreate[0]).toLowerCase()
					);
					if (existingParent && existingParent.items) {
						findOrCreateParent(
							existingParent.items,
							toCreate.slice(1)
						);
					} else {
						const parent: NavigationEntry = {
							title: this.toSentence(toCreate[0]),
							items: [],
						};
						level.push(parent);
						findOrCreateParent(parent.items, toCreate.slice(1));
					}
				};

				findOrCreateParent(navContent.menu, parents);
			}

			// Generate updated navigation.yml
			const copyright = navigationString.match(/(#.{0,}\n)+\n/).first();
			const updatedNavigation = copyright + stringifyYaml(navContent);

			this.app.vault.modify(navigation as TFile, updatedNavigation);

			if (old.includes("Untitled")) {
				// @ts-ignore
				this.app.commands.executeCommandById(
					"templater-obsidian:templates/default-page-template.md"
				);
			}

			new Notice(
				`Updated navigation.yml with ${newTitle} at ${newPath}. Make sure links are updated too.`
			);
		}
	};

	toKebab = (text: string) => {
		return text.replace(/[ ]/g, "-").toLowerCase();
	};

	toSentence = (text: string) => {
		return text
			.split("-")
			.map((s) => s[0].toUpperCase() + s.substr(1))
			.join(" ");
	};

	lookForNavigation = (parent: TFolder): TAbstractFile | undefined => {
		const navigation = parent.children?.find((f) =>
			f.name.includes("navigation.yml")
		);
		if (navigation) return navigation;
		if (parent.parent) {
			return this.lookForNavigation(parent.parent);
		}
	};

	formatImages = (e: Editor) => {
		const { line } = e.getCursor();
		const lineContent = e.getLine(line);
		const mdImage = /\!\[\]\(.+(images\/.+)\)/g.exec(lineContent);
		if (mdImage) {
			const image = mdImage[1];
			e.replaceRange(
				`![](${image})`,
				{
					line,
					ch: mdImage.index,
				},
				{
					line,
					ch: mdImage.index + mdImage.first().length,
				}
			);
		}
	};

	formatLinks = (e: Editor) => {
		// Internal links should not have markdown extension and start with /
		const { line } = e.getCursor();
		const lineContent = e.getLine(line);
		const mdLink = /\(.+\.md\)/g.exec(lineContent);
		if (mdLink) {
			const link = mdLink.first();
			e.replaceRange(
				link.replace(".md", "").replace("(", "(/"),
				{
					line,
					ch: mdLink.index,
				},
				{
					line,
					ch: mdLink.index + link.length,
				}
			);
		}
	};

	formatFile = debounce(
		(e: Editor) => {
			const content = e
				.getValue()
				.replace(/(\<[^\>]+\>)/g, "<!-- prettier-ignore -->\n$1");

			if (!prettier.check(content, prettierOptions)) {
				const cursor = e.getCursor();
				const { top: scrollTop } = e.getScrollInfo();
				const output = prettier.formatWithCursor(content, {
					...prettierOptions,
					cursorOffset: e.posToOffset(cursor),
					// @ts-ignore
					rangeStart: undefined,
				});
				e.setValue(
					output.formatted.replace(
						/\<\!\-\- prettier\-ignore \-\-\>/g,
						""
					)
				);
				e.setCursor(e.offsetToPos(output.cursorOffset));
				e.scrollTo(undefined, scrollTop);
			}

			const file = this.app.workspace.getActiveFile();
			this.onRename(file, file.path);
		},
		8000,
		true
	);

	onunload() {}
}
