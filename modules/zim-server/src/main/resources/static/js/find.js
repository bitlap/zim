var showAddFriend;
var showAddGroup;
layui.use([ 'element', 'jquery', 'layer', 'form', 'upload', 'flow'],function() {
    var element = layui.element, $ = layui.jquery, form = layui.form(), layer = layui.layer,flow = layui.flow;
    //屏蔽右键菜单
    $(document).bind("contextmenu",function(e){
        return false;
    });
    //父级窗口的对象
    var socket = parent.socket, layim = parent.layim;
    //显示添加好友面板
    showAddFriend = function(item) {
        var mine = layim.cache().mine;
        var $item = $(item);
        var img = $item.find("img").attr("src");
        var username = $item.find("cite").text();
        var id = $item.attr("layim-data-uid");
        var index = layim.add({
            type: 'friend' //friend：申请加好友、group：申请加群
            ,username: username //好友昵称，若申请加群，参数为：groupname
            ,avatar: img
            ,submit: function(group, remark, index){ //一般在此执行Ajax和WS，以通知对方
                socket.send(JSON.stringify({
                    type:"addFriend",
                    mine:mine,
                    to:{"id":id},
                    msg:JSON.stringify({"groupId":group,"remark":remark,"type":"0"})
                }));
                layer.msg('申请已发送，请等待对方确认', {icon: 1,shade: 0.5}, function(){layer.close(index);});
            }
        });
    }

    var id = layim.cache().mine.id;
    $("#createGroup").click(function(){
        layer.open({
            type: 2,
            title: "创建群组",
            area: ['400px', '270px'], //宽高
            content: '/static/html/creategroup.html?createId='+id,
            success: function(layero, index){
            }
        });
    })

     $("#createUserGroup").click(function(){
            layer.open({
                type: 2,
                title: "创建分组",
                area: ['400px', '200px'], //宽高
                content: '/static/html/createusergroup.html?createId='+id,
                success: function(layero, index){
                }
            });
        })
    //显示添加群面板
    showAddGroup = function(item) {
        var mine = layim.cache().mine;
        var $item = $(item);
        var img = $item.find("img").attr("src");
        var groupname = $item.find("cite").text();
        var id = $item.attr("layim-data-uid");
        var groupId = $item.attr("layim-data-group-id");
        console.log($item);
        var index = layim.add({
            type: 'group' //friend：申请加好友、group：申请加群
            ,groupname: groupname //好友昵称，若申请加群，参数为：groupname
            ,avatar: img
            ,submit: function(group, remark, index){ //一般在此执行Ajax和WS，以通知对方
                socket.send(JSON.stringify({
                    type:"addGroup",
                    mine:mine,
                    to:{"id":id},
                    msg:JSON.stringify({"groupId":groupId,"remark":remark,"type":"0"})
                }));
                layer.msg('申请已发送，请等待对方确认', {icon: 1,shade: 0.5}, function(){layer.close(index);});
            }
        });
    }
    //显示我创建的群
    $("#myGroup").click(function () {
        var createId = id;
        $("#groups").remove();
        $(".layui-group").append("<ul id='groups'></ul>");
        flow.load({
            elem: '#groups'
            ,done: function(page, next){
                var lis = [];
                var params = 'page='+page;
                if(createId != null && "" != createId) {
                    params += "&createId=" + createId;
                }
                $.get('/user/findMyGroups?' + params , function(res){
                    // res = eval("(" + res + ")");
                    layui.each(res.data, function(index, item){
                        var img = '<img style="width: 40px; height: 40px; border-radius: 100%;" src ="' + item.avatar + '"/>';
                        var cite = '<cite style="display: block;padding-top:10px; font-size: 14px;">' + item.groupname + '</cite>';
                        var a = '<a style="cursor:pointer" layim-data-group-id=' + item.id + ' layim-data-uid=' + item.createId + ' onclick="showAddGroup(this);">' + img + cite + '</a>';
                        var li = '<li class="layim-user" style="margin:20px 20px;display: inline-block;">'+ a +' </li>';
                        lis.push(li);
                    });
                    next(lis.join(''), page < res.pages);
                });
            }
        });
    })
    //找人
    $("#findFriend").click(function(){
        $("#users").remove();
        $(".layui-friend").append("<ul id='users'></ul>");
        var name = $("input[name='friend_name']").val();
        var sex = $("input[type='radio']:checked").val();
        flow.load({
            elem: '#users'
            ,done: function(page, next){
                var lis = [];
                var params = "sex="+ sex;
                if(name != null && "" != name) {
                    params += "&name=" + name;
                }
                $.get('/user/findUsers?' + params + '&page='+page, function(res){
                    // res = eval("(" + res + ")");
                    layui.each(res.data, function(index, item){
                        var img = '<img style="width: 40px; height: 40px; border-radius: 100%;" src ="' + item.avatar + '"/>';
                        var cite = '<cite style="display: block;padding-top:10px; font-size: 14px;">' + item.username + '</cite>';
                        var a = '<a style="cursor:pointer" layim-data-uid=' + item.id + ' onclick="showAddFriend(this);">' + img + cite + '</a>';
                        var li = '<li class="layim-user" style="margin:20px 20px;display: inline-block;">'+ a +' </li>';
                        lis.push(li);
                    });
                    next(lis.join(''), page < res.pages);
                });
            }
        });
    });
    //找群
    $("#findGroup").click(function(){
        var name = $("input[name='group_name']").val();
        $("#groups").remove();
        $(".layui-group").append("<ul id='groups'></ul>");
        flow.load({
            elem: '#groups'
            ,done: function(page, next){
                var lis = [];
                var params = 'page='+page;
                if(name != null && "" != name) {
                    params += "&name=" + name;
                }
                $.get('/user/findGroups?' + params , function(res){
                    // res = eval("(" + res + ")");
                    layui.each(res.data, function(index, item){
                        var img = '<img style="width: 40px; height: 40px; border-radius: 100%;" src ="' + item.avatar + '"/>';
                        var cite = '<cite style="display: block;padding-top:10px; font-size: 14px;">' + item.groupname + '</cite>';
                        var a = '<a style="cursor:pointer" layim-data-group-id=' + item.id + ' layim-data-uid=' + item.createId + ' onclick="showAddGroup(this);">' + img + cite + '</a>';
                        var li = '<li class="layim-user" style="margin:20px 20px;display: inline-block;">'+ a +' </li>';
                        lis.push(li);
                    });
                    next(lis.join(''), page < res.pages);
                });
            }
        });
    })
});