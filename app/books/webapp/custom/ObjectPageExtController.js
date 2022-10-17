sap.ui.define([
    "sap/m/MessageToast",
    "sap/m/MessageBox"
],
    function (MessageToast, MessageBox) {
        "use strict";
        return {
            customEdit1: function (oEvent) {
                var oModel = this.getModel();
                var oBookContext = this.getBindingContext();
                var oAction = oModel.bindContext("CatalogService.customEditBoundAction(...)", oBookContext);
                oAction.setParameter("bookType", "01");
                oAction.setParameter("comment", "comment from JS custom action");
                oAction.execute().then(
                    function () {
                        MessageToast.show("Book created for comment " + oBookContext.getProperty("comment"));
                    },
                    function (oError) {
                        MessageBox.alert(oError.message, {
                            icon: MessageBox.Icon.ERROR,
                            title: "Error"
                        });
                    });
            }
        };
    });
