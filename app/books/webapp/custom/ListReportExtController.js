sap.ui.define([],
    function () {
        "use strict";
        return {
            customEdit3: function (oEvent) {
                var oModel = this.getModel();
                // create new list binding in JS
                // var oListBinding = oModel.bindList("/Books", undefined, undefined, undefined, {$select:"bookNo,title,stock,comment"});
                // oListBinding.getContexts(0, 10);

                this.refresh();
            }
        };
    });
