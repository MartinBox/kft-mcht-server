var editFlag = false;
var editIndex = undefined;

$.extend($.fn.datagrid.methods, {
    editCell: function (jq, param) {
        return jq.each(function () {
            var opts = $(this).datagrid('options');
            var fields = $(this).datagrid('getColumnFields', true).concat($(this).datagrid('getColumnFields'));
            for (var i = 0; i < fields.length; i++) {
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor1 = col.editor;
                if (fields[i] != param.field) {
                    col.editor = null;
                }
            }
            $(this).datagrid('beginEdit', param.index);
            for (var i = 0; i < fields.length; i++) {
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor = col.editor1;
            }
        });
    }
});

function endEditing() {
    if (editIndex == undefined) {
        return true
    }
    if ($('#dg').datagrid('validateRow', editIndex)) {
        $('#dg').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}

function onClickCell(index, field) {
    // for "onClickCell: onClickCell"
    if (!editFlag) {
        return;
    }

    if (endEditing()) {
        $('#dg').datagrid('selectRow', index).datagrid('editCell', {
            index: index,
            field: field
        });
        editIndex = index;
    }

    undo_update();
}

function onClickRow(index) {
    // for "onClickRow: onClickRow"
    if (!editFlag) {
        return;
    }
    if (editIndex != index) {
        if (edit()) {
            $('#dg').datagrid('selectRow', index).datagrid('beginEdit', index);
            editIndex = index;
        } else {
            $('#dg').datagrid('selectRow', editIndex);
        }
    }

    undo_update();
}

function restore() {
    $("#tb-undo").linkbutton("disable");
    $("#tb-add").linkbutton("disable");
    $("#tb-remove").linkbutton("disable");
    $("#tb-save").linkbutton("disable");
}

function undo_update() {
    if ($('#dg').datagrid('getChanges').length > 0) {
        $("#tb-undo").linkbutton("enable");
    } else {
        $("#tb-undo").linkbutton("disable");
    }
}

function refresh() {
    restore();
    editIndex = undefined;
    $('#dg').datagrid('reload');
    $('#dg').datagrid('clearChecked');
}

function undo() {
    restore();
    editIndex = undefined;
    $('#dg').datagrid('rejectChanges');
    if (editFlag) {
        edit();
        $("#tb-edit").linkbutton("unselect");
    }
}

function edit() {
    if (!endEditing()) {
        $.messager.show({
            title: 'Tips',
            msg: 'Unvalid data.',
            icon: 'info'
        });
        $("#tb-edit").linkbutton("select");
        return;
    }

    editFlag = !editFlag;

    if (editFlag) {
        undo_update();
        $("#tb-add").linkbutton("enable");
        $("#tb-remove").linkbutton("enable");
        $("#tb-save").linkbutton("disable");
        $("#tb-edit").linkbutton({
            text: '编辑中',
            selected: true,
            iconCls: "icon-lock"
        });
    } else {
        if ($('#dg').datagrid('getChanges').length > 0) {
            $("#tb-undo").linkbutton("enable");
            $("#tb-save").linkbutton("enable");
        } else {
            $("#tb-undo").linkbutton("disable");
            $("#tb-save").linkbutton("disable");
        }
        $("#tb-add").linkbutton("disable");
        $("#tb-remove").linkbutton("disable");
        $("#tb-edit").linkbutton({
            text: '编辑',
            selected: false,
            iconCls: "icon-edit"
        });
    }

    $('#dg').datagrid('clearChecked');
}

function add() {
    if (!editFlag) {
        $.messager.show({
            title: 'Tips',
            msg: 'Cannot added or deleted under non edit status.',
            timeout: 1000,
            showType: 'slide'
        });
        return;
    }

    if (endEditing()) {
        $('#dg').datagrid('endEdit', $('#dg').datagrid('getRows').length - 1);
        $('#dg').datagrid('appendRow', {});
        editIndex = $('#dg').datagrid('getRows').length - 1;
        $('#dg').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);

        undo_update();
    } else {
        $.messager.alert({
            title: 'Tips',
            msg: 'Cannot added one more blank line.',
            icon: 'info'
        });
        return;
    }
}

function remove() {
    if (!editFlag) {
        $.messager.show({
            title: 'Tips',
            msg: 'Cannot added or deleted under non edit status.',
            timeout: 2000,
            showType: 'slide'
        });
        return
    }
    var row = $('#dg').datagrid('getChecked');

    if (row.length === 0) {
        $.messager.alert({
            title: 'Tips',
            msg: 'no rows selected.',
            icon: 'info'
        });
    } else {
        for (var x of row) {
            $('#dg').datagrid('cancelEdit', $('#dg').datagrid('getRowIndex', x)).datagrid('deleteRow', $('#dg').datagrid('getRowIndex', x));
        }

        endEditing();
        editIndex = undefined;

        undo_update();
    }
}

$(document).ready(function () {
    restore();
});

var toolbar = [{
        text: '刷新',
        iconCls: 'icon-reload',
        handler: refresh
    }, '-', {
        text: '撤销',
        iconCls: 'icon-undo',
        handler: undo
    }, '-', {
        text: '编辑',
        iconCls: 'icon-edit',
        toggle: true,
        handler: edit
    }, '-', {
        text: '新增',
        disabled: false,
        iconCls: 'icon-add',
        handler: add
    }, '-', {
        text: '删除',
        disabled: false,
        iconCls: 'icon-remove',
        handler: remove
    }, '-', {
        text: '保存',
        disabled: true,
        iconCls: 'icon-save',
        handler: save
    }, '-', {
        text: '单选',
        iconCls: 'icon-ok',
        handler: function () {
            alert($(this).text());
        }
    }
];