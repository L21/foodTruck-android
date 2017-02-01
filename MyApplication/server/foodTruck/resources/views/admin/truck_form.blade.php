@extends("admin.layout")

@section("content")
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header">
            Truck
            <small><a href="/truck/">&laquo; Back to List Page</a></small>
        </h1>

        @include("admin.message")

        <form action="/truck<?php if ($truck->id) {
            echo "/".$truck->id;
        }?>" method="post">
            <div class="editor row">
                <div class="col-sm-9">
                    <?php if($truck->id):?><input type="hidden" name="_method" value="PUT"><?php endif;?>
                    <input type="hidden" name="_token" value="{{ csrf_token() }}">

                    <h3>Truck Information</h3>

                    <div class="form-group">
                        <label for="exampleInputEmail1">Truck Name</label>
                        <input type="text" name="truckName" class="form-control" id="exampleInputEmail1"
                               value="<?php echo $truck->truckName;?>">
                    </div>
					<div class="form-group">
                        <label for="exampleInputEmail1">Owner Id</label>
                        <input type="text" name="ownerId" class="form-control" id="exampleInputEmail1"
                               value="<?php echo $truck->ownerId;?>">
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
