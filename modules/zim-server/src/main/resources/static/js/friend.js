function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = unescape(window.location.search).substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}
layui.use(['jquery', 'form'], function(layim){
    var $ = layui.jquery,form = layui.form;
    $(document).ready(function() {
        var username = getQueryString("username");
        var sign = getQueryString("sign");
        var avatar = getQueryString("avatar");
        var email = getQueryString("email");
        var sex = getQueryString("sex");
        if (sex == "1") {
            $("#sex").val("男");
        } else {
            $("#sex").val("女");
        }
        $("#username").val(username);
        $("#LAY_demo_upload").attr("src", avatar);
        $("#email").val(email);
        $("#sign").val(sign);
    });
});