<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;

class menuController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        if($request->has('app')){
			$builder = \App\Menu::orderBy('id', 'desc')->where('truckId',$request->truckId);
			$menus = $builder->get();
			return $menus;
		}else{
			$builder = \App\Menu::orderBy('id', 'desc');
		$builder = $builder->with('truck');
        $menus = $builder->paginate();

        return view('admin/menus_list', ['menus' => $menus]);
		}
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        $menu = new \App\Menu;

        return view('admin/menu_form', [
            'menu' => $menu
        ]);
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
       
		$menu=new \App\Menu();
		$menu->name=$request->name;
		$truck=\App\Truck::where('id',$request->truckId)->first();
		if($truck){
        $menu->truckId=$request->truckId;
		}else{
			return redirect()->back()->withDanger('Truck Id is invalid!');
		}
		$menu->price=$request->price;
		$menu->save();

        return redirect('menu')->withSuccess('Successfully added!');

    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        $menu = \App\Menu::findOrFail($id);

        return view('admin/menu_form', [
            'menu' => $menu
        ]);
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
        $menu = \App\Menu::findOrFail($id);
		$truck=\App\Truck::where('id',$request->truckId)->first();
		if($truck){
        $menu->truckId=$request->truckId;
		}else{
			return redirect()->back()->withDanger('Truck Id is invalid!');
		}
		$menu->name=$request->name;
        $menu->truckId=$request->truckId;
		$menu->price=$request->price;
		$menu->save();
		return redirect('menu')->withSuccess('Successfully saved!');

    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $menu = \App\Menu::findOrFail($id);
		
		$menu->delete();

        return redirect()->back()->withDanger('Has been deleted!');
    }
}
