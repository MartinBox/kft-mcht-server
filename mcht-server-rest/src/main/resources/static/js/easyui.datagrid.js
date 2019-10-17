$(document).ready(function () {
    $('#dg').datagrid({
        onBeforeLoad: function (param) {},
        onLoadError: function () {
            $.messager.alert({
                title: "Tips",
                msg: "load data fail",
                icon: "error"
            });
            return false;
        }
    });

    $('#dg').datagrid("options").loadFilter = function (data) {
        if (data.hasOwnProperty("total")) {
            if (data.total === 0) {
                return {
                    total: 0,
                    rows: []
                };
            } else {
                return data;
            }
        } else {
            $.messager.alert({
                title: "Tips",
                msg: data.errDesc,
                icon: "error"
            });
            return {
                total: 0,
                rows: []
            };
        }
    };
});
