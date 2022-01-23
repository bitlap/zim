var uid = location.href.split("?")[1].split("&")[0].split("=")[1];
layui.use(['layim', 'flow'], function(){
    var layim = parent.layim
        ,layer = layui.layer
        ,laytpl = layui.laytpl
        ,$ = layui.jquery
        ,flow = layui.flow;

    var socket = parent.socket;
    var cache = {}; //用于临时记录请求到的数据
    //请求消息
    var renderMsg = function(page, callback){
        //实际部署时，请将下述 getmsg.json 改为你的接口地址
        $.get('/user/findAddInfo?uid=' + uid, { page: page || 1 }, function(res){
            if(res.code !== 0){
                return layer.msg(res.msg);
            }
            //记录来源用户信息
            layui.each(res.data, function(index, item){
                cache[item.from] = item.user;
            });
            callback && callback(res.data, res.pages);
        },"json");
    };

    //消息信息流
    flow.load({
        elem: '#LAY_view' //流加载容器
        ,isAuto: false
        ,end: '<li class="layim-msgbox-tips">暂无更多新消息</li>'
        ,done: function(page, next){ //加载下一页
            renderMsg(page, function(data, pages){
                var html = laytpl(LAY_tpl.value).render({
                    data: data
                    ,page: page
                });
                next(html, page < pages);
            });
        }
    });

    //操作
    var active = {
        //拒绝加群
        refuseAddGroup: function(othis) {
            var li = othis.parents('li'),uid = li.data('uid'),messageBoxId = li.data('messageboxid');
            layer.confirm("确定拒绝?",{icon:2,title:"提示"},function(index){
                socket.send(JSON.stringify({
                    type:"refuseAddGroup",
                    msg: JSON.stringify({
                            toUid: uid,
                            messageBoxId: messageBoxId,
                            mine: layim.cache().mine
                        }
                    )
                }));
                layer.close(index);
                othis.parent().html('<em>已拒绝</em>');
            });
        },
        //同意添加群
        agreeAddGroup: function(othis){
            var li = othis.parents('li')
                ,uid = li.data('uid')
                ,from_group = li.data('fromgroup')
                ,user = cache[uid]
                ,messageBoxId = li.data('messageboxid');
            //同意该用户添加群
            socket.send(JSON.stringify({
                type:"agreeAddGroup",
                msg: JSON.stringify({
                        toUid: uid,
                        groupId: from_group,
                        messageBoxId: messageBoxId,
                        mine:layim.cache().mine
                    }
                )
            }));
            othis.parent().html('已同意');
        },
        //同意添加好友
        agreeAddFriend: function(othis){
            var li = othis.parents('li')
                ,uid = li.data('uid')
                ,from_group = li.data('fromgroup')
                ,user = cache[uid]
                ,messageBoxId = li.data('messageboxid');
            //选择分组
            parent.layui.layim.setFriendGroup({
                type: 'friend'
                ,username: user.username
                ,avatar: user.avatar
                ,group: parent.layui.layim.cache().friend //获取好友分组数据
                ,submit: function(group, index){
                    //实际部署时，请开启下述注释，并改成你的接口地址
                    $.post('/user/agreeFriend', {
                        uid: uid //对方用户ID
                        ,from_group: from_group //对方设定的好友分组
                        ,group: group //我设定的好友分组
                        ,messageBoxId: messageBoxId
                    }, function(res){
                        if(res.code !== 0){
                            return layer.msg(res.msg);
                        }
                        layer.msg(res.msg);
                        //将好友追加到主面板
                        parent.layui.layim.addList({
                            type: 'friend'
                            ,avatar: user.avatar //好友头像
                            ,username: user.username //好友昵称
                            ,groupid: group //所在的分组id
                            ,id: uid //好友ID
                            ,sign: user.sign //好友签名
                        });
                        //通知对方添加我
                        socket.send(JSON.stringify({
                            type:"agreeAddFriend",
                            mine:layim.cache().mine,
                            to:{"id":uid},
                            msg:'{"group":'+from_group +'}'
                        }));
                        parent.layer.close(index);
                        othis.parent().html('已同意');
                    },"json");

                }
            });
        }

        //拒绝添加好友
        ,refuseAddFriend: function(othis){
            var li = othis.parents('li')
                ,messageBoxId = li.data('messageboxid')
                , uid = li.data('uid')
            layer.confirm('确定拒绝吗？', function(index){
                $.post('/user/refuseFriend', {
                    messageBoxId: messageBoxId,
                    to: uid
                }, function(res){
                    if(res.code !== 0){
                        return layer.msg(res.msg);
                    }
                    layer.close(index);
                    othis.parent().html('<em>已拒绝</em>');
                },"json");
            });
        }
    };

    $('body').on('click', '.layui-btn', function(){
        var othis = $(this), type = othis.data('type');
        active[type] ? active[type].call(this, othis) : '';
    });
});