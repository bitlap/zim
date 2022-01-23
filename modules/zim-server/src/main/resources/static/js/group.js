layui.use(['jquery', 'layer', 'form', 'upload'], function () {
    var $ = layui.jquery, form = layui.form(), layer = layui.layer, upload = layui.upload;
    var layim = parent.parent.layim;
    layui.upload({
        url: '/user/upload/groupAvatar',
        methos: 'post'
        , title: '上传群头像'
        , ext: 'jpg|png|gif',
        before: function (input) {
            console.log("before upload!");
        },
        success: function (res) {
            if (0 == res.code) {
                $("#LAY_demo_upload").attr('src', res.data.src);
                $("#group_avatar").val(res.data.src);
                layer.msg("上传成功", {time: 2000});
            } else {
                layer.msg(res.msg, {time: 2000});
            }
        }
    });

    //获取缓存的用户id 未知原因，找不到cache属性
    // var createId = layim.cache().mine.id;
    var param = GetQueryString("createId");
    var createId;
    if (param != null && param.toString().length > 1) {
        createId = GetQueryString("createId");
    }
    console.info("user { id: "+createId+"}");
    if(createId == null){
        return false;
    }

    $("#restusergroup").click(function () {
        layer.ready(function () {
        $("#usergroupname").val("");
      })
           });
    $("#restgroup").click(function () {
           layer.ready(function () {
           $("#groupname").val("");
           $("#LAY_demo_upload").attr('src', "");
           $("#group_avatar").val("");
           })
        });
    $("#userbtn").click(function () {
        layer.ready(function () {
            var groupname = $("#usergroupname").val();
            if ('' === groupname) {
                layer.tips('分组名称不能为空', '#usergroupname');
                return;
            }
            var d = {'groupname': groupname, 'uid': createId};
            //发送
            $.ajax({
                url: "../../user/createUserGroup",
                dataType: "JSON",
                contentType: "application/json",
                type: "POST",
                data: JSON.stringify(d),
                success: function (res) {
                    layer.msg(res.msg, {time: 2000}, function () {
                        window.parent.parent.location.reload();//刷新主页面,简单粗暴的方法,否则应该操作表单元素太麻烦
                    });
                },
                error: function (data) {
                    layer.msg("服务器错误,请稍后再试！");
                }
            });
            return false;
        });
    });
    //提交修改项
    $("#btn").click(function () {
        layer.ready(function () {
            var groupname = $("#groupname").val();
            var group_avatar = $("#group_avatar").val();
            if ('' === groupname) {
                layer.tips('群名称不能为空', '#groupname');
                return;
            }
            if ('' === group_avatar) {
                layer.tips('群头像不能为空', '#avatar');
                return;
            }
            var d = {'groupname': groupname, 'avatar': group_avatar, 'createId': createId};
            //发送
            $.ajax({
                url: "../../user/createGroup",
                dataType: "JSON",
                contentType: "application/json",
                type: "POST",
                data: JSON.stringify(d),
                success: function (res) {
                    if (res.code == 0 && res.data != -1) {
                        layim.addList({
                            type: 'group'
                            , avatar: window.location.protocol + '//' + window.location.host + group_avatar
                            , groupname: groupname
                            , id: res.data + ''
                        });
                    }
                    layer.msg(res.msg, {time: 2000}, function () {
                        window.parent.location.reload()
                    });
                },
                error: function (data) {
                    layer.msg("服务器错误,请稍后再试！");
                }
            });
            return false;
        });
    });

});

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);//search,查询？后面的参数，并匹配正则
    if (r != null) return unescape(r[2]);
    return null;
}