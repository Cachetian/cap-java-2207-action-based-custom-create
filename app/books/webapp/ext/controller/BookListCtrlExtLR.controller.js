sap.ui.define(['sap/ui/core/mvc/ControllerExtension'], function (ControllerExtension) {
  'use strict';

  return ControllerExtension.extend('bestbook2207.books.ext.controller.BookListCtrlExtLR', {
    // this section allows to extend lifecycle hooks or hooks provided by Fiori elements
    override: {
      /**
             * Called when a controller is instantiated and its View controls (if available) are already created.
             * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
             * @memberOf bestbook2207.books.ext.controller.BookListCtrlExtLR
             */
      onInit: function () {
        // you can access the Fiori elements extensionAPI via this.base.getExtensionAPI
        var oModel = this.base.getExtensionAPI().getModel();
      },

      onAfterRendering: function () {
        var oTable = this.base.getExtensionAPI().byId("bestbook2207.books::BooksList--fe::table::Books::LineItem");
        oTable.attachBeforeExport({}, (e) => {
          let exportSettings = e.getParameter("exportSettings");
          console.log(`event.exportSettings: ${exportSettings}`);
          exportSettings.workbook.columns[4].utc=false;
          let userExportSettings = e.getParameter("userExportSettings");
          console.log(`event.userExportSettings: ${userExportSettings}`);
        });
      }
    }

  });
});
