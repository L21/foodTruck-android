<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;

class orderController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $builder = \App\Order::orderBy('id', 'desc');
		$builder = $builder->with('truck')->with('user')->with('menu');
        $orders = $builder->paginate();

        return view('admin/orders_list', ['orders' => $orders]);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        return view("order_form");
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $order=new \App\Order;
		$order->userId=$request->userId;
		$order->menuId=$request->menuId;
		$order->truckId=$request->truckId;
		$order->status=1;
		$order->save();
		$truck=\App\Truck::where('id',$request->truckId)->first();
		return response()->json([
				'truckToken' => $truck->owner->oneSignalToken,
				'orderId'=>$order->id
		]);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $order=\App\Order::where('id',$id)->get();
		return (String)$order;
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $order = \App\Order::findOrFail($id);
		$order->delete();

        return redirect()->back()->withDanger('Has been deleted!');
    }
}
