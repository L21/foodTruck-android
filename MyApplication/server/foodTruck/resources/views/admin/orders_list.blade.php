@extends("admin.layout")

@section("head")
    <script type="text/javascript" src="/static/js/vendor/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $('#cb').click(function () {
                $('.cb').prop('checked', this.checked);
            });
        });
        function batchDelete() {
            var values = $('input:checkbox:checked.cb').map(function () {
                return this.value;
            }).get();
            if (values.length == 0) {
                alert("Please choose first");
                return false;
            }
            if (!confirm("Confirm?")) {
                return false;
            }
            var csrf_token = $('meta[name=csrf-token]').attr('content');
            $('<form id="batch" action="/order/batch-delete" method="POST"></form>').appendTo('body');
            $('<input>').attr({type: 'hidden', name: 'ids', value: values}).appendTo('#batch');
            $('<input>').attr({type: 'hidden', name: '_token', value: csrf_token}).appendTo('#batch');
            $('#batch').submit();
            return false;
        }
    </script>
@stop

@section("content")
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header">Orders</h1>

        @include("admin.message")

        <?php echo $orders->render();?>
        <div>
            <div class="btn-group">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false"> Batch Operations <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li><a href="#" onclick="return batchDelete();">Delete</a></li>
                </ul>
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th><input title="All" type="checkbox" id="cb"></th>
                    <th>#</th>
                    <th>User Email</th>
                    <th>User Phone</th>
					<th>Truck Name</th>
					<th>Food Name</th>
					<th>Price</th>
					<th>Status</th>
                    <th>Created Date</th>
                    <th>&nbsp;</th>
                </tr>
                </thead>
                <tbody>
                <?php foreach($orders as $order):?>
                <tr>
                    <td><input title="Choose" class="cb" type="checkbox" value="<?php echo $order->id;?>" name="cb[]"></td>
                    <td><?php echo $order->id;?></td>
                    <td><?php if($order->user):?>
                        <?php echo $order->user->email;?>
                        <?php endif;?></td>
					
                    <td><?php if($order->user):?><?php echo htmlspecialchars($order->user->phone);?><?php endif;?></td>
					
					
					<td><?php if($order->truck):?><?php echo htmlspecialchars($order->truck->truckName);?><?php endif;?></td>
					
					
					<td><?php if($order->menu):?><?php echo htmlspecialchars($order->menu->name);?><?php endif;?></td>
					
					
					<td><?php if($order->menu):?><?php echo htmlspecialchars($order->menu->price);?><?php endif;?></td>
					
					<td><?php if($order->status==1):?>Confirmed<?php endif;?><?php if($order->status==2):?>Ready to Pickup<?php endif;?><?php if($order->status==3):?>Finished<?php endif;?></td>
                    <td><?php echo \App\Toolkits\Display::showDate($order->created_at);?></td>
                    <td>
                        <div>
                            <form action="/order/<?php echo $order->id;?>" method="post"
                                  onsubmit="return confirm('Confirm?');">
                                <input type="hidden" name="_method" value="DELETE">
                                <input type="hidden" name="_token" value="{{ csrf_token() }}">

                                <button class="btn btn-xs btn-danger" type="submit"><span
                                            class="glyphicon glyphicon-trash"></span></button>
                            </form>
                        </div>
                    </td>
                </tr>
                <?php endforeach;?>
                </tbody>
            </table>
        </div>
        <?php echo $orders->render();?>

    </div>
@stop
