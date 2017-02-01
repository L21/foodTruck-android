@extends("admin.layout")

@section("content")
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header">
            Menu
            <small><a href="/menu/">&laquo; Back to List Page</a></small>
        </h1>

        @include("admin.message")

        <form action="/menu<?php if ($menu->id) {
            echo "/".$menu->id;
        }?>" method="post">
            <div class="editor row">
                <div class="col-sm-9">
                    <?php if($menu->id):?><input type="hidden" name="_method" value="PUT"><?php endif;?>
                    <input type="hidden" name="_token" value="{{ csrf_token() }}">

                    <h3>Menu Information</h3>

                    <div class="form-group">
                        <label for="exampleInputEmail1">Truck Id</label>
                        <input type="text" name="truckId" class="form-control" id="exampleInputEmail1"
                               value="<?php echo $menu->truckId;?>">
                    </div>
					<div class="form-group">
                        <label for="exampleInputEmail1">Name</label>
                        <input type="text" name="name" class="form-control" id="exampleInputEmail1"
                               value="<?php echo $menu->name;?>">
                    </div>
                    <div class="form-group">
                        <label for="exampleInputEmail1">Price</label>
                        <input type="text" name="price" class="form-control" id="exampleInputEmail1"
                               value="<?php echo $menu->price;?>" >
                        
                    </div>

                   
 <div class="form-group">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>

                </div>
                <div class="col-sm-3">
                   
  
                </div>
            </div>
        </form>


    </div>
@stop
