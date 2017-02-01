@extends("admin.layout")

@section("content")
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header">
            用户
            <small><a href="/manager/users/">&laquo; 返回列表</a></small>
        </h1>

        @include("admin.message")

        <h3>帐号信息</h3>

        <div class="table-responsive">
            <table class="table table-striped">
                <tbody>
                <tr>
                    <th>头像</th>
                    <td><?php if($user->thumbnail):?><img width="180" height="180" src="<?php echo $user->thumbnail;?>"
                                                          alt=""><?php endif;?></td>
                </tr>
                <tr>
                    <th>昵称</th>
                    <td><?php echo $user->nickname;?></td>
                </tr>
                <tr>
                    <th>登录帐号</th>
                    <td><?php echo $user->auths->first()->identifier;?></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
@stop
