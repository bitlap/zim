layui.use(['jquery', 'layer', 'form', 'upload'], function () {
    var $ = layui.jquery,
        layim = parent.layim,
        layer = layui.layer;
    form = layui.form();
    //屏蔽右键菜单
    $(document).bind("contextmenu", function (e) {
        return false;
    });

    var id = layim.cache().mine.id;
    //修改头像
    layui.upload({
        url: "/user/updateAvatar"
        , title: '修改头像'
        , ext: 'jpg|png|gif'
        , before: function (input) {
            console.log("before upload!");
        }
        , success: function (res, input) {
            if (0 == res.code) {
                $("#LAY_demo_upload").attr('src', res.data.src);
                $("#user_avatar").val(res.data.src);
                layer.msg("修改成功", {time: 2000}, function () {
                    window.parent.location.reload();//刷新父页面
                });
            } else {
                layer.msg(res.msg, {time: 2000});
            }
        }
    });

    //从缓存中初始化数据
    $(document).ready(function () {
        var mine = layim.cache().mine;
        $("#username").val(mine.username);
        $("#email").val(mine.email);
        $("#oldpwd").val("");
        $("#pwd").val("");
        $("#repwd").val("");
        $("#sign").val(mine.sign);
        $("#LAY_demo_upload").attr("src", mine.avatar);
        if (mine.sex == "0") {
            $("input[type='radio']").eq(0).attr("checked", true);
        }
       else{
            $("input[type='radio']").eq(1).attr("checked", true);
        }
        form.render();//重新渲染 可以解决多种没有显示的情况
    });

    function ajaxUpdateUserInfo(d) {
        //发送
        $.ajax({
            url: "../../user/updateInfo",
            dataType: "JSON",
            contentType: "application/json",
            type: "POST",
            data: JSON.stringify(d),
            success: function (data) {
                if (data.code == 1) {
                    layer.msg(data.msg, {time: 2000}, function () {
                        window.parent.location.reload();//刷新父页面
                    });
                } else if (data.code == 0) {
                    layer.msg(data.msg, {time: 2000}, function () {
                        window.parent.location.reload();//刷新父页面
                    });
                }
            },
            error: function (data) {
                layer.msg("服务器错误,请稍后再试！");
            }
        });
        return false;
    }

    //提交修改项
    $("#btn").click(function () {
        layer.ready(function () {
            var username = $("#username").val();
            // var email = $("#email").val();
            var sign = $("#sign").val();
            // var avatar = $("#user_avatar").val();
            var sex = $("input[name='sex']:checked").val();
            var updatepw = $("input[name='updatepw']:checked").val();
            if ('' === username) {
                layer.tips('用户名不能为空', '#username');
                return;
            }
            // if('' == email){
            //     layer.tips('邮箱不能为空!', '#email');
            //     return ;
            // }
            if ('' === sign) {
                layer.tips('签名不能为空', '#sign');
            }
            var oldpwd = $("#oldpwd").val(); //旧密码
            var pwd = $("#pwd").val();
            var repwd = $("#repwd").val();
            if ('' != oldpwd) {
                if ('' === pwd) {
                    layer.tips('新密码不能为空', '#pwd');
                    return;
                }
                if ('' != pwd && '' == repwd) {
                    layer.tips('重复密码不能为空', '#repwd');
                    return;
                }
                if (!/^[\S]{6,12}$/.test(oldpwd)) {
                    layer.tips('密码必须6到12位', '#oldpwd');
                    return;
                }
                if ('' != pwd && '' != repwd && '' == oldpwd) {
                    layer.tips('必须输入旧密码', '#oldpwd');
                    return;
                }
                if ('' != pwd && '' != repwd && '' != oldpwd && pwd != repwd) {
                    layer.tips('两次密码不一致', '#pwd');
                    return;
                }
                if ('' != pwd && '' != repwd && '' != oldpwd && pwd == repwd) {
                    if (!/^[\S]{6,12}$/.test(pwd)) {
                        layer.tips('密码必须6到12位', '#pwd');
                        return;
                    }
                    if (!/^[\S]{6,12}$/.test(repwd)) {
                        layer.tips('密码必须6到12位', '#repwd');
                        return;
                    }
                }
                var d = {
                    'id': id,
                    'username': username,
                    'sex': sex,
                    'password': repwd,
                    'oldpwd': oldpwd,
                    'sign': sign
                };
                //发送
                ajaxUpdateUserInfo(d)
                return false;
            } else {
                if ('yes' === updatepw) {
                    layer.alert("sorry，您勾选了修改密码但未做任何修改！")
                    return false;
                }
                var d = {'id': id, 'username': username, 'sex': sex, 'sign': sign};
                //发送
                ajaxUpdateUserInfo(d)
                return false;
            }
        });
    });
});