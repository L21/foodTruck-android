<div class="col-sm-3 col-md-2 sidebar">
    <ul class="nav nav-sidebar">
        <!--<li <?php if(\Request::is('manager')){echo 'class="active"';}?>><a href="/manager/">概述</a></li>-->
        <li <?php if(\Request::is('user*')){echo 'class="active"';}?>><a href="/user/">Users</a></li>
        <li <?php if(\Request::is('truck*')){echo 'class="active"';}?>><a href="/truck/">Trucks</a></li>
        <li <?php if(\Request::is('order*')){echo 'class="active"';}?>><a href="/order/">Orders</a></li>
		<li <?php if(\Request::is('menu*')){echo 'class="active"';}?>><a href="/menu/">Menus</a></li>
    </ul>
	<!--
    <ul class="nav nav-sidebar">
        <li <?php if(\Request::is('manager/projects*')){echo 'class="active"';}?>><a href="/manager/projects/">用户项目</a></li>
        <li <?php if(\Request::is('manager/appointments*')){echo 'class="active"';}?>><a href="/manager/appointments/">导师预约申请</a></li>
        <li <?php if(\Request::is('manager/jobs*')){echo 'class="active"';}?>><a href="/manager/jobs/">招募令</a></li>
    </ul>
    <ul class="nav nav-sidebar">
        <li <?php if(\Request::is('manager/comments*')){echo 'class="active"';}?>><a href="/manager/comments/">用户评论</a></li>
        <li <?php if(\Request::is('manager/feedbacks*')){echo 'class="active"';}?>><a href="/manager/feedbacks/">用户反馈</a></li>
        <li <?php if(\Request::is('manager/versions*')){echo 'class="active"';}?>><a href="/manager/versions/">App Version</a></li>
    </ul>
	<!--
    <ul class="nav nav-sidebar">
        <li <?php if(\Request::is('manager/captchas*')){echo 'class="active"';}?>><a href="/manager/captchas/">手机验证码</a></li>
        <li <?php if(\Request::is('manager/masters*')){echo 'class="active"';}?>><a href="/manager/masters/">导师</a></li>
        <li <?php if(\Request::is('manager/users*')){echo 'class="active"';}?>><a href="/manager/users/">用户</a></li>
        <li <?php if(\Request::is('manager/admins*')){echo 'class="active"';}?>><a href="/manager/admins/">管理员</a></li>
    </ul>
	-->
</div>
