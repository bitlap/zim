layui.use(['layim', 'flow', 'element'], function(){
    var layim = parent.layim,layer = layui.layer,$ = layui.jquery,flow = layui.flow,element = layui.element();
    var socket = parent.socket;

    element.on('tab(setting)', function(){
        console.log($(this).text() + this.getAttribute('lay-id'));
    });
});