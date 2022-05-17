/*
THIS IS A GENERATED/BUNDLED FILE BY ESBUILD
if you want to view the source, please visit the github repository of this plugin
*/

var __create = Object.create;
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __getProtoOf = Object.getPrototypeOf;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __markAsModule = (target) => __defProp(target, "__esModule", { value: true });
var __export = (target, all) => {
  __markAsModule(target);
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __reExport = (target, module2, desc) => {
  if (module2 && typeof module2 === "object" || typeof module2 === "function") {
    for (let key of __getOwnPropNames(module2))
      if (!__hasOwnProp.call(target, key) && key !== "default")
        __defProp(target, key, { get: () => module2[key], enumerable: !(desc = __getOwnPropDesc(module2, key)) || desc.enumerable });
  }
  return target;
};
var __toModule = (module2) => {
  return __reExport(__markAsModule(__defProp(module2 != null ? __create(__getProtoOf(module2)) : {}, "default", module2 && module2.__esModule && "default" in module2 ? { get: () => module2.default, enumerable: true } : { value: module2, enumerable: true })), module2);
};
var __async = (__this, __arguments, generator) => {
  return new Promise((resolve, reject) => {
    var fulfilled = (value) => {
      try {
        step(generator.next(value));
      } catch (e) {
        reject(e);
      }
    };
    var rejected = (value) => {
      try {
        step(generator.throw(value));
      } catch (e) {
        reject(e);
      }
    };
    var step = (x) => x.done ? resolve(x.value) : Promise.resolve(x.value).then(fulfilled, rejected);
    step((generator = generator.apply(__this, __arguments)).next());
  });
};

// main.ts
__export(exports, {
  default: () => CustomAttachmentLocation
});
var import_obsidian = __toModule(require("obsidian"));
var Path = __toModule(require("path"));
var DEFAULT_SETTINGS = {
  attachmentFolderPath: "./assets/${filename}",
  pastedImageFileName: "image-${date}",
  dateTimeFormat: "YYYYMMDDHHmmssSSS",
  autoRenameFolder: true,
  autoRenameFiles: false
};
var originalSettings = {
  attachmentFolderPath: ""
};
var blobToArrayBuffer = (blob) => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onloadend = () => resolve(reader.result);
    reader.readAsArrayBuffer(blob);
  });
};
var TemplateString = class extends String {
  interpolate(params) {
    const names = Object.keys(params);
    const vals = Object.values(params);
    return new Function(...names, `return \`${this}\`;`)(...vals);
  }
};
var CustomAttachmentLocation = class extends import_obsidian.Plugin {
  constructor() {
    super(...arguments);
    this.useRelativePath = false;
  }
  onload() {
    return __async(this, null, function* () {
      console.log("loading plugin");
      this.adapter = this.app.vault.adapter;
      yield this.loadSettings();
      this.backupConfigs();
      this.addSettingTab(new CustomAttachmentLocationSettingTab(this.app, this));
      this.registerEvent(this.app.workspace.on("editor-paste", this.handlePaste.bind(this)));
      this.registerEvent(this.app.workspace.on("editor-drop", this.handleDrop.bind(this)));
      this.registerEvent(this.app.vault.on("rename", this.handleRename.bind(this)));
    });
  }
  onunload() {
    console.log("unloading plugin");
    this.restoreConfigs();
  }
  loadSettings() {
    return __async(this, null, function* () {
      this.settings = Object.assign({}, DEFAULT_SETTINGS, yield this.loadData());
      if (this.settings.attachmentFolderPath.startsWith("./"))
        this.useRelativePath = true;
      else
        this.useRelativePath = false;
    });
  }
  saveSettings() {
    return __async(this, null, function* () {
      yield this.saveData(this.settings);
    });
  }
  backupConfigs() {
    originalSettings.attachmentFolderPath = this.app.vault.getConfig("attachmentFolderPath");
  }
  restoreConfigs() {
    this.app.vault.setConfig("attachmentFolderPath", originalSettings.attachmentFolderPath);
  }
  updateAttachmentFolderConfig(path) {
    this.app.vault.setConfig("attachmentFolderPath", path);
  }
  getAttachmentFolderPath(mdFileName) {
    let path = new TemplateString(this.settings.attachmentFolderPath).interpolate({
      filename: mdFileName
    });
    return path;
  }
  getAttachmentFolderFullPath(mdFolderPath, mdFileName) {
    let attachmentFolder = "";
    if (this.useRelativePath)
      attachmentFolder = Path.join(mdFolderPath, this.getAttachmentFolderPath(mdFileName));
    else {
      attachmentFolder = this.getAttachmentFolderPath(mdFileName);
    }
    return (0, import_obsidian.normalizePath)(attachmentFolder);
  }
  getPastedImageFileName(mdFileName) {
    let datetime = (0, import_obsidian.moment)().format(this.settings.dateTimeFormat);
    let name = new TemplateString(this.settings.pastedImageFileName).interpolate({
      filename: mdFileName,
      date: datetime
    });
    return name;
  }
  handlePaste(event, editor, view) {
    return __async(this, null, function* () {
      console.log("Handle Paste");
      let mdFileName = view.file.basename;
      let mdFolderPath = Path.dirname(view.file.path);
      let path = this.getAttachmentFolderPath(mdFileName);
      let fullPath = this.getAttachmentFolderFullPath(mdFolderPath, mdFileName);
      this.updateAttachmentFolderConfig(path);
      let clipBoardData = event.clipboardData;
      let clipBoardItems = clipBoardData.items;
      if (!clipBoardData.getData("text/plain")) {
        for (let i in clipBoardItems) {
          if (!clipBoardItems.hasOwnProperty(i))
            continue;
          let item = clipBoardItems[i];
          if (item.kind !== "file")
            continue;
          if (!(item.type === "image/png" || item.type === "image/jpeg"))
            continue;
          let pasteImage = item.getAsFile();
          if (!pasteImage)
            continue;
          let extension = "";
          item.type === "image/png" ? extension = "png" : item.type === "image/jpeg" && (extension = "jpeg");
          event.preventDefault();
          if (!(yield this.adapter.exists(fullPath)))
            yield this.adapter.mkdir(fullPath);
          let img = yield blobToArrayBuffer(pasteImage);
          let name = this.getPastedImageFileName(mdFileName);
          let imageFile = yield this.app.saveAttachment(name, extension, img);
          let markdownLink = yield this.app.fileManager.generateMarkdownLink(imageFile, view.file.path);
          markdownLink += "\n\n";
          editor.replaceSelection(markdownLink);
        }
      }
    });
  }
  handleDrop(event, editor, view) {
    return __async(this, null, function* () {
      console.log("Handle Drop");
      let mdFileName = view.file.basename;
      let mdFolderPath = Path.dirname(view.file.path);
      let path = this.getAttachmentFolderPath(mdFileName);
      let fullPath = this.getAttachmentFolderFullPath(mdFolderPath, mdFileName);
      if (!this.useRelativePath && !(yield this.adapter.exists(fullPath)))
        yield this.app.vault.createFolder(fullPath);
      this.updateAttachmentFolderConfig(path);
    });
  }
  handleRename(newFile, oldFilePath) {
    return __async(this, null, function* () {
      var _a;
      console.log("Handle Rename");
      if (!this.settings.autoRenameFolder || newFile.extension !== "md")
        return;
      let newName = newFile.basename;
      let oldName = Path.basename(oldFilePath, ".md");
      let mdFolderPath = Path.dirname(newFile.path);
      let oldAttachmentFolderPath = this.getAttachmentFolderFullPath(mdFolderPath, oldName);
      let newAttachmentFolderPath = this.getAttachmentFolderFullPath(mdFolderPath, newName);
      if ((yield this.adapter.exists(oldAttachmentFolderPath)) && oldAttachmentFolderPath !== newAttachmentFolderPath) {
        let tfolder = this.app.vault.getAbstractFileByPath(oldAttachmentFolderPath);
        if (tfolder == null)
          return;
        yield this.app.fileManager.renameFile(tfolder, newAttachmentFolderPath);
        this.updateAttachmentFolderConfig(this.getAttachmentFolderPath(newName));
      }
      if (!this.settings.autoRenameFiles)
        return;
      let embeds = (_a = this.app.metadataCache.getCache(newFile.path)) == null ? void 0 : _a.embeds;
      if (!embeds)
        return;
      let files = [];
      for (let embed of embeds) {
        let link = embed.link;
        if (link.endsWith(".png") || link.endsWith("jpeg"))
          files.push(Path.basename(link));
        else
          continue;
      }
      let attachmentFiles = yield this.adapter.list(newAttachmentFolderPath);
      for (let file of attachmentFiles.files) {
        console.log(file);
        let filePath = file;
        let fileName = Path.basename(filePath);
        if (files.indexOf(fileName) > -1 && fileName.contains(oldName)) {
          fileName = fileName.replace(oldName, newName);
          let newFilePath = (0, import_obsidian.normalizePath)(Path.join(newAttachmentFolderPath, fileName));
          let tfile = this.app.vault.getAbstractFileByPath(filePath);
          yield this.app.fileManager.renameFile(tfile, newFilePath);
        } else
          continue;
      }
    });
  }
};
var CustomAttachmentLocationSettingTab = class extends import_obsidian.PluginSettingTab {
  constructor(app, plugin) {
    super(app, plugin);
    this.plugin = plugin;
  }
  display() {
    let { containerEl } = this;
    containerEl.empty();
    containerEl.createEl("h2", { text: "Custom Attachment Location" });
    let el = new import_obsidian.Setting(containerEl).setName("Location for New Attachments").setDesc('Start with "./" to use relative path. Available variables: ${filename}.(NOTE: DO NOT start with "/" or end with "/". )').addText((text) => text.setPlaceholder("./assets/${filename}").setValue(this.plugin.settings.attachmentFolderPath).onChange((value) => __async(this, null, function* () {
      console.log("attachmentFolder: " + value);
      value = (0, import_obsidian.normalizePath)(value);
      console.log("normalized attachmentFolder: " + value);
      this.plugin.settings.attachmentFolderPath = value;
      if (value.startsWith("./"))
        this.plugin.useRelativePath = true;
      else
        this.plugin.useRelativePath = false;
      yield this.plugin.saveSettings();
    })));
    el.controlEl.addEventListener("change", () => {
      this.display();
    });
    new import_obsidian.Setting(containerEl).setName("Pasted Image Name").setDesc("Available variables: ${filename}, ${date}.").addText((text) => text.setPlaceholder("image-${date}").setValue(this.plugin.settings.pastedImageFileName).onChange((value) => __async(this, null, function* () {
      console.log("pastedImageFileName: " + value);
      this.plugin.settings.pastedImageFileName = value;
      yield this.plugin.saveSettings();
    })));
    new import_obsidian.Setting(containerEl).setName("Date Format").setDesc("YYYYMMDDHHmmssSSS").addMomentFormat((text) => text.setDefaultFormat("YYYYMMDDHHmmssSSS").setValue(this.plugin.settings.dateTimeFormat).onChange((value) => __async(this, null, function* () {
      console.log("dateTimeFormat: " + value);
      this.plugin.settings.dateTimeFormat = value || "YYYYMMDDHHmmssSSS";
      yield this.plugin.saveSettings();
    })));
    new import_obsidian.Setting(containerEl).setName("Automatically rename attachment folder").setDesc('When renaming md files, automatically rename attachment folder if folder name contains "${filename}".').addToggle((toggle) => toggle.setValue(this.plugin.settings.autoRenameFolder).onChange((value) => __async(this, null, function* () {
      this.plugin.settings.autoRenameFolder = value;
      this.display();
      yield this.plugin.saveSettings();
    })));
    if (this.plugin.settings.autoRenameFolder)
      new import_obsidian.Setting(containerEl).setName("Automatically rename attachment files [Experimental]").setDesc('When renaming md files, automatically rename attachment files if file name contains "${filename}".').addToggle((toggle) => toggle.setValue(this.plugin.settings.autoRenameFiles).onChange((value) => __async(this, null, function* () {
        this.plugin.settings.autoRenameFiles = value;
        yield this.plugin.saveSettings();
      })));
  }
};
