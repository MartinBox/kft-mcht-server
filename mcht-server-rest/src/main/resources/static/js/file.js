function file_upload(url, file_id, options) {
    options = options || {};
    files = document.getElementById(file_id);

    var form = new FormData();
    for (var i = 0, file; file = files.files[i]; i++) {
        form.append(files.name, file);
        for (var key in options) {
            form.append(key, options[key]);
        }
    }
    var xhr = new XMLHttpRequest();
    xhr.upload.addEventListener("progress", upload_progess, false);
    xhr.addEventListener("load", upload_over, false);
    xhr.addEventListener("error", upload_fail, false);
    xhr.addEventListener("abort", upload_cancel, false);
    xhr.open("POST", url);
    xhr.send(form);
}

function upload_progess(evt) {
    if (evt.lengthComputable) {
        var value = Math.round(evt.loaded * 100 / evt.total);
        $('#progressbar').progressbar('setValue', value);

        if (value === 100) {
            $('#progressbar').innerHTML = '上传完成';
        }
    } else {
        $('#progressbar').innerHTML = '无法计算';
    }
}

function upload_over(evt) {
    var rsp = JSON.parse(evt.target.responseText);

    if (rsp.errCode === "000") {
        $.messager.alert({
            title: 'Tips',
            msg: 'upload success',
            icon: 'info'
        });
    } else {
        $.messager.alert({
            title: 'Tips',
            msg: rsp.errDesc,
            icon: 'error'
        });
    }
}

function upload_fail(evt) {
    $.messager.alert({
        title: 'Tips',
        msg: 'upload error.',
        icon: 'error'
    });
}

function upload_cancel(evt) {
    console.log('upload cancel');
}

function file_download(url) {
    // default method: get
    console.log(url);
    var file = window.open(url, "_blank", "height=0,width=0,toolbar=no,menubar=no,scrollbars=no,resizable=on,location=no,status=no");
    file.document.execCommand("SaveAs");
    file.window.close();
    file.close();
}

function page_download(file_name, file_content) {
    var blob = new Blob([file_content]);
    var link = document.createElement('a');
    var evt = document.createEvent("HTMLEvents");

    evt.initEvent("click", false, false);
    link.download = file_name;
    link.href = URL.createObjectURL(blob);
    link.dispatchEvent(evt);
}

function url_download(file_name, url) {
    var link = document.createElement('a');
    var evt = document.createEvent("HTMLEvents");

    evt.initEvent("click", false, false);
    link.href = url;
    link.download = file_name;
    link.dispatchEvent(evt);
}