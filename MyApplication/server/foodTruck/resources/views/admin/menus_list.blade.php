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
            $('<form id="batch" action="/menu/batch-delete" method="POST"></form>').appendTo('body');
            $('<input>').attr({type: 'hidden', name: 'ids', value: values}).appendTo('#batch');
            $('<input>').attr({type: 'hidden', name: '_token', value: csrf_token}).appendTo('#batch');
            $('#batch').submit();
            return false;
        }
    </script>
@stop

@section("content")
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header">Menus
		<small><a href="/menu/create"> New+ &raquo;</a></small>
		</h1>
		
        @include("admin.message")

        <?php echo $menus->render();?>
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
                    <th>Name</th>
                    <th>Truck Name</th>
					<th>Price</th>
					
                    <th>Created Date</th>
                    <th>&nbsp;</th>
                </tr>
                </thead>
                <tbody>
                <?php foreach($menus as $menu):?>
                <tr>
                    <td><input title="Choose" class="cb" type="checkbox" value="<?php echo $menu->id;?>" name="cb[]"></td>
                    <td><?php echo $menu->id;?></td>
					<td><?php echo htmlspecialchars($menu->name);?></td>
                    <td><?php echo htmlspecialchars($menu->truck->truckName);?></td>
					<td><?php echo htmlspecialchars($menu->price);?></td>
					
					
                    <td><?php echo \App\Toolkits\Display::showDate($menu->created_at);?></td>
                    <td>
                        <div>
                            <form action="/menu/<?php echo $menu->id;?>" method="post"
                                  onsubmit="return confirm('Confirm?');">
                                <input type="hidden" name="_method" value="DELETE">
                                <input type="hidden" name="_token" value="{{ csrf_token() }}">
								 <a href="/menu/<?php echo $menu->id;?>/edit" class="btn btn-xs btn-info"><span
                                            class="glyphicon glyphicon-pencil"></span></a>
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
        <?php echo $menus->render();?>

    </div>
@stop
