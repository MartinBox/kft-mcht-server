var ajax = $.ajax;
$.extend({
    ajax: function (url, options) {
        if (typeof url === 'object') {
            options = url;
            url = undefined;
        }
        options = options || {};
        url = options.url;
        var xsrftoken = $('meta[name=_xsrf]').attr('content');
        var headers = options.headers || {};
        var domain = document.domain.replace(/\./ig, '\\.');
        if (!/^(http:|https:).*/.test(url) || eval('/^(http:|https:)\\/\\/(.+\\.)*' + domain + '.*/').test(url)) {
            headers = $.extend(headers, {
                'X-Xsrftoken': xsrftoken
            });
        }
        options.headers = headers;
        return ajax(url, options);
    }
});

Date.prototype.format = function (format) {
    var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
}

function ajaxRequest(url, type, data, button, process) {
    $.ajax({
        url: url,
        type: type,
        data: data,
        dataType: "json",
        beforeSend: function () {
            if (button.length > 0) {
                $('#button').linkbutton("disable");
            }
        },
        complete: function () {
            if (button.length > 0) {
                $('#button').linkbutton("enable");
            }
        },
        error: function (data) {
            $.messager.alert({
                title: "Tips",
                msg: data.responseText,
                icon: "error"
            });
        },
        success: function (rsp, status, xhr) {
            process();
        }
    });
}

function strNvl(str) {
    if (str === undefined || str === null) {
        return "";
    }

    return str;
}

function str2Money(money, n) {
    n = n > 0 && n <= 20 ? n : 2;

    var str = (parseFloat((money + "").replace(/[^\d\.-]/g, "")) / 100).toFixed(n) + "";
    var l = str.split(".")[0].split("").reverse();
    var r = str.split(".")[1];

    var t = "";

    for (i = 0; i < l.length; i++) {
        t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
    }

    return t.split("").reverse().join("") + "." + r;
}

function getComboboxText(arr, key) {
    for (var i = 0, len = arr.length; i < len; i++) {
        if (arr[i].value === key) {
            return arr[i].text;
        }
    }

    return "";
}

var treatyStatus = [
    {'value': '-1', 'text': '未发起'},
    {'value': '0', 'text': '待复核'},
    {'value': '1', 'text': '生效'},
    {'value': '2', 'text': '失败'},
    {'value': '3', 'text': '冻结'},
    {'value': '6', 'text': '解约'}
];
var txStatus = [
    {'value': '0', 'text': '成功'},
    {'value': '1', 'text': '请求中'},
    {'value': '4', 'text': '失败'}
];

var cardType = [
    {'value': '1', 'text': '借记卡'},
    {'value': '2', 'text': '信用卡'}
];

var acctType = [
    {'value': '1', 'text': '个人'},
    {'value': '2', 'text': '企业'}
];

var paperType = [
    {'value': '0', 'text': '公民身份证'},
    {'value': '1', 'text': '中国护照'},
    {'value': '2', 'text': '军人身份证'},
    {'value': '3', 'text': '警官证'},
    {'value': '4', 'text': '户口簿'},
    {'value': '5', 'text': '临时身份证'},
    {'value': '6', 'text': '外国护照'},
    {'value': '7', 'text': '港澳通行证'},
    {'value': '8', 'text': '台胞通行证'},
    {'value': '9', 'text': '离休干部荣誉证'},
    {'value': 'A', 'text': '军官退休证'},
    {'value': 'B', 'text': '文职干部退休证'},
    {'value': 'C', 'text': '军事院校学员证'},
    {'value': 'D', 'text': '武装警察身份证'},
    {'value': 'E', 'text': '军官证'},
    {'value': 'F', 'text': '文职干部证'},
    {'value': 'G', 'text': '军人士兵证'},
    {'value': 'H', 'text': '武警士兵证'},
    {'value': 'Z', 'text': '其他证件'}
];
var paymentItemType = [
    {'value': '00000', 'text': '通用费项'},
    {'value': '00100', 'text': '电费'},
    {'value': '00101', 'text': '家用电费'},
    {'value': '00102', 'text': '生产用电费'},
    {'value': '00200', 'text': '水暖费'},
    {'value': '00201', 'text': '用水费'},
    {'value': '00202', 'text': '排水费'},
    {'value': '00203', 'text': '直饮水费'},
    {'value': '00400', 'text': '电话费'},
    {'value': '00401', 'text': '市内电话费'},
    {'value': '00402', 'text': '长途电话费'},
    {'value': '00403', 'text': '移动电话费'},
    {'value': '00500', 'text': '通讯费'}
];

var bankCode = [
    {'value': '0025840', 'text': '邮政储蓄银行'},
    {'value': '1021000', 'text': '工商银行'},
    {'value': '1031000', 'text': '农业银行'},
    {'value': '1041000', 'text': '中国银行'},
    {'value': '1051000', 'text': '建设银行'},
    {'value': '2011000', 'text': '国家开发银行'},
    {'value': '2011001', 'text': '中国进出口银行'},
    {'value': '2011002', 'text': '中国农业发展银行'},
    {'value': '3011000', 'text': '交通银行'},
    {'value': '3021000', 'text': '中信银行'},
    {'value': '3031000', 'text': '光大银行'},
    {'value': '3041000', 'text': '华夏银行'},
    {'value': '3051000', 'text': '民生银行'},
    {'value': '3065810', 'text': '广发银行'},
    {'value': '3085840', 'text': '招商银行'},
    {'value': '3091000', 'text': '兴业银行'},
    {'value': '3102900', 'text': '浦东发展银行'},
    {'value': '3131000', 'text': '北京银行股份有限公司'},
    {'value': '3131100', 'text': '天津银行股份有限公司'},
    {'value': '3131460', 'text': '廊坊银行股份有限公司'},
    {'value': '3131920', 'text': '包商银行股份有限公司'},
    {'value': '3132210', 'text': '盛京银行'},
    {'value': '3132611', 'text': '哈尔滨银行股份有限公司'},
    {'value': '3133010', 'text': '江苏银行股份有限公司'},
    {'value': '3133011', 'text': '南京银行股份有限公司'},
    {'value': '3133310', 'text': '杭州银行股份有限公司'},
    {'value': '3133320', 'text': '宁波银行股份有限公司'},
    {'value': '3133330', 'text': '温州银行股份有限公司'},
    {'value': '3133387', 'text': '浙江稠州商业银行股份有限公司'},
    {'value': '3133450', 'text': '台州银行股份有限公司'},
    {'value': '3133451', 'text': '浙江泰隆商业银行'},
    {'value': '3134510', 'text': '齐鲁银行'},
    {'value': '3134910', 'text': '中原银行股份有限公司'},
    {'value': '3135810', 'text': '广州银行股份有限公司'},
    {'value': '3135840', 'text': '平安银行'},
    {'value': '3135841', 'text': '上海银行'},
    {'value': '3135850', 'text': '珠海华润银行股份有限公司'},
    {'value': '3135910', 'text': '广东南粤银行股份有限公司'},
    {'value': '3136020', 'text': '东莞银行股份有限公司'},
    {'value': '3136530', 'text': '重庆银行'},
    {'value': '3138810', 'text': '乌鲁木齐市商业银行中亚支行'},
    {'value': '3141100', 'text': '天津滨海农村商业银行股份有限公司'},
    {'value': '3143020', 'text': '无锡农村商业银行股份有限公司'},
    {'value': '3143022', 'text': '江苏江阴农村商业银行股份有限公司'},
    {'value': '3143040', 'text': '江苏江南农村商业银行股份有限公司'},
    {'value': '3143051', 'text': '太仓农村商业银行'},
    {'value': '3143052', 'text': '昆山农村商业银行'},
    {'value': '3143054', 'text': '吴江农村商业银行'},
    {'value': '3143055', 'text': '江苏常熟农村商业银行股份有限公司'},
    {'value': '3143056', 'text': '张家港农村商业银行'},
    {'value': '3145810', 'text': '广州农村商业银行股份有限公司'},
    {'value': '3145880', 'text': '佛山顺德农村商业银行股份有限公司'},
    {'value': '3146410', 'text': '海口农村商业银行股份有限公司'},
    {'value': '3146510', 'text': '成都农村商业银行股份有限公司'},
    {'value': '3146530', 'text': '重庆农村商业银行股份有限公司'},
    {'value': '3154560', 'text': '恒丰银行'},
    {'value': '3163310', 'text': '浙商银行股份有限公司'},
    {'value': '3171100', 'text': '天津农村商业银行'},
    {'value': '3181100', 'text': '渤海银行股份有限公司'},
    {'value': '3205840', 'text': '深圳龙岗鼎业村镇银行股份有限公司'},
    {'value': '3205841', 'text': '深圳宝安融兴村镇银行有限责任公司'},
    {'value': '3205842', 'text': '深圳福田银座村镇银行股份有限公司'},
    {'value': '3205843', 'text': '深圳南山宝生村镇银行股份有限公司'},
    {'value': '3205844', 'text': '深圳宝安桂银村镇银行股份有限公司'},
    {'value': '3205845', 'text': '深圳龙岗国安村镇银行有限责任公司'},
    {'value': '3222900', 'text': '上海农村商业银行'},
    {'value': '4021000', 'text': '北京农村商业银行股份有限公司'},
    {'value': '4021210', 'text': '河北省农村信用社联合社'},
    {'value': '4021610', 'text': '山西省农村信用社联合社'},
    {'value': '4021910', 'text': '内蒙古自治区农村信用社联合社'},
    {'value': '4022210', 'text': '辽宁省农村信用社联合社'},
    {'value': '4022410', 'text': '吉林省农村信用社联合社'},
    {'value': '4022610', 'text': '黑龙江省农村信用社联合社'},
    {'value': '4023010', 'text': '江苏省农村信用社联合社'},
    {'value': '4023310', 'text': '浙江省农村信用社联合社'},
    {'value': '4023320', 'text': '宁波鄞州农村合作银行'},
    {'value': '4023610', 'text': '安徽省农村信用联社'},
    {'value': '4023910', 'text': '福建省农村信用社联合社'},
    {'value': '4024210', 'text': '江西省农村信用社联合社'},
    {'value': '4024510', 'text': '山东省农村信用社联合社'},
    {'value': '4024910', 'text': '河南省农村信用社联合社'},
    {'value': '4025210', 'text': '湖北省农村信用社联合社'},
    {'value': '4025211', 'text': '武汉农村商业银行股份有限公司'},
    {'value': '4025510', 'text': '湖南省农村信用社联合社'},
    {'value': '4025810', 'text': '广东省农村信用社联合社'},
    {'value': '4025840', 'text': '深圳农村商业银行'},
    {'value': '4026020', 'text': '东莞农村商业银行股份有限公司'},
    {'value': '4026110', 'text': '广西壮族自治区农村信用社联合社'},
    {'value': '4026410', 'text': '海南省农村信用社联合社'},
    {'value': '4026510', 'text': '四川省农村信用社联合社'},
    {'value': '4027010', 'text': '贵州省农村信用社联合社'},
    {'value': '4027310', 'text': '云南省农村信用社联合社'},
    {'value': '4027910', 'text': '陕西省农村信用社联合社'},
    {'value': '4028210', 'text': '甘肃省农村合作社联合社'},
    {'value': '4028510', 'text': '青海省农村信用社联合社'},
    {'value': '4028710', 'text': '宁夏黄河农村商业银行股份有限公司'},
    {'value': '4028810', 'text': '新疆维吾尔自治区农村信用社联合社'},
    {'value': '5012900', 'text': '汇丰银行(中国）有限公司'},
    {'value': '5022900', 'text': '东亚银行(中国）有限公司'},
    {'value': '5032900', 'text': '南洋商业银行有限公司'},
    {'value': '5045840', 'text': '恒生银行(中国）有限公司'},
    {'value': '5085840', 'text': '大众银行'},
    {'value': '5095840', 'text': '星展银行（中国）有限公司'},
    {'value': '5105840', 'text': '永亨银行(中国）有限公司'},
    {'value': '5315840', 'text': '花旗银行(中国）有限公司'},
    {'value': '5615840', 'text': '三菱东京日联银行（中国）有限公司'},
    {'value': '5632900', 'text': '三井住友银行(中国)有限公司'},
    {'value': '5642900', 'text': '瑞穗实业银行(中国）有限公司'},
    {'value': '5931000', 'text': '友利银行（中国）有限公司'},
    {'value': '5951000', 'text': '新韩银行(中国)有限公司'},
    {'value': '5961000', 'text': '企业银行(中国)有限公司'},
    {'value': '6715840', 'text': '渣打银行(中国）有限公司'},
    {'value': '7855840', 'text': '华商银行'},
    {'value': '7872900', 'text': '华一银行'},
    {'value': '3135210', 'text': '汉口银行'},
    {'value': '3135860', 'text': '广东华兴银行'},
    {'value': '3138210', 'text': '兰州银行股份有限公司'},
    {'value': '3136510', 'text': '成都银行'},
    {'value': '3193610', 'text': '徽商银行'},
    {'value': '3137910', 'text': '西安银行'},
    {'value': '3131210', 'text': '河北银行'},
    {'value': '3131310', 'text': '邢台银行'},
    {'value': '3131340', 'text': '保定银行'},
    {'value': '3131380', 'text': '张家口银行'},
    {'value': '3131410', 'text': '承德银行'},
    {'value': '3131430', 'text': '沧州银行'},
    {'value': '3131480', 'text': '衡水市商业银行'},
    {'value': '3131630', 'text': '阳泉市商业银行'},
    {'value': '3131680', 'text': '晋城银行'},
    {'value': '3131910', 'text': '内蒙古银行'},
    {'value': '3131930', 'text': '乌海银行'},
    {'value': '3132230', 'text': '鞍山银行'},
    {'value': '3132240', 'text': '抚顺银行'},
    {'value': '3132260', 'text': '丹东银行'},
    {'value': '3132270', 'text': '锦州银行'},
    {'value': '3132280', 'text': '营口沿海银行'},
    {'value': '3132290', 'text': '阜新银行'},
    {'value': '3132310', 'text': '辽阳银行'},
    {'value': '3132330', 'text': '铁岭银行'},
    {'value': '3132340', 'text': '朝阳银行'},
    {'value': '3132410', 'text': '吉林银行'},
    {'value': '3132610', 'text': '龙江银行'},
    {'value': '3133050', 'text': '苏州银行'},
    {'value': '3133123', 'text': '江苏长江商业银行'},
    {'value': '3133350', 'text': '嘉兴银行'},
    {'value': '3133360', 'text': '湖州银行'},
    {'value': '3133380', 'text': '金华银行'},
    {'value': '3133454', 'text': '浙江民泰商业银行'},
    {'value': '3133930', 'text': '厦门银行'},
    {'value': '3134210', 'text': '南昌银行'},
    {'value': '3134280', 'text': '赣州银行'},
    {'value': '3134330', 'text': '上饶银行'},
    {'value': '3134520', 'text': '青岛银行'},
    {'value': '3134530', 'text': '齐商银行'},
    {'value': '3134550', 'text': '东营银行'},
    {'value': '3134580', 'text': '潍坊银行'},
    {'value': '3134610', 'text': '济宁银行'},
    {'value': '3134650', 'text': '威海市商业银行'},
    {'value': '3134680', 'text': '德州银行'},
    {'value': '3134730', 'text': '临商银行'},
    {'value': '3134732', 'text': '日照银行'},
    {'value': '3134911', 'text': '郑州银行'},
    {'value': '3134950', 'text': '平顶山银行'},
    {'value': '3134980', 'text': '新乡银行'},
    {'value': '3135010', 'text': '焦作中旅银行'},
    {'value': '3135211', 'text': '湖北银行'},
    {'value': '3135510', 'text': '华融湘江银行'},
    {'value': '3136110', 'text': '广西北部湾银行'},
    {'value': '3136140', 'text': '柳州银行'},
    {'value': '3136170', 'text': '桂林银行'},
    {'value': '3136560', 'text': '攀枝花市商业银行'},
    {'value': '3136570', 'text': '泸州市商业银行'},
    {'value': '3136590', 'text': '绵阳市商业银行'},
    {'value': '3136650', 'text': '乐山市商业银行'},
    {'value': '3136710', 'text': '宜宾市商业银行'},
    {'value': '3136770', 'text': '雅安市商业银行'},
    {'value': '3136840', 'text': '凉山州商业银行'},
    {'value': '3137010', 'text': '贵阳银行'},
    {'value': '3137011', 'text': '贵州银行股份有限公司'},
    {'value': '3137310', 'text': '富滇银行'},
    {'value': '3137911', 'text': '长安银行'},
    {'value': '3138211', 'text': '甘肃银行'},
    {'value': '3138710', 'text': '宁夏银行'},
    {'value': '3138720', 'text': '石嘴山银行'},
    {'value': '3138811', 'text': '乌鲁木齐银行'},
    {'value': '3138820', 'text': '昆仑银行'},
    {'value': '3138840', 'text': '哈密市商业银行'},
    {'value': '3138981', 'text': '新疆汇和银行'},
    {'value': '3205846', 'text': '深圳光明沪农商村镇银行股份有限公司'},
    {'value': '4021911', 'text': '内蒙古呼和浩特金谷农村商业银行'},
    {'value': '4022611', 'text': '大兴安岭农村商业银行股份有限公司'},
    {'value': '4023011', 'text': '徐州淮海农村商业银行'},
    {'value': '4023012', 'text': '江苏盐城农村商业银行'},
    {'value': '4023311', 'text': '浙江萧山农村商业银行'},
    {'value': '4023321', 'text': '新疆绿洲国民村镇银行'},
    {'value': '4023611', 'text': '合肥科技农村商业银行'},
    {'value': '4023911', 'text': '福鼎市农村信用合作联社'},
    {'value': '4023912', 'text': '福建石狮农村商业银行'},
    {'value': '4024211', 'text': '南昌农村商业银行'},
    {'value': '4024511', 'text': '青岛农村商业银行'},
    {'value': '4024512', 'text': '济南农村商业银行'},
    {'value': '4025811', 'text': '佛山农村商业银行'},
    {'value': '4028811', 'text': '新疆农村信用社'},
    {'value': '4028812', 'text': '新疆天山农村商业银行'},
    {'value': '4028813', 'text': '疏附县农村信用合作联社'},
    {'value': '5135840', 'text': '大新银行'},
    {'value': '5971000', 'text': '韩亚银行'},
    {'value': '6222900', 'text': '大华银行'},
    {'value': '3146411', 'text': '海口联合农村商业银行'},
    {'value': '3134211', 'text': '江西银行'},
    {'value': '3133910', 'text': '福建海峡银行'},
    {'value': '3131610', 'text': '晋商银行'},
    {'value': '3132220', 'text': '大连银行'},
    {'value': '3216671', 'text': '长沙银行'}
];

function cssReload(url) {
    var link = document.createElement("link");
    link.type = "text/css";
    link.rel = "stylesheet";
    link.href = url;
    document.getElementsByTagName("head")[0].appendChild(link);
}