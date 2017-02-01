<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;

class truckController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
		if($request->has('app')){
			$builder = \App\Truck::orderBy('id', 'desc');
			$trucks = $builder->get();
			return $trucks;
		}else{
			$builder = \App\Truck::orderBy('id', 'desc');
		$builder = $builder->with('owner');
        $trucks = $builder->paginate();

        return view('admin/trucks_list', ['trucks' => $trucks]);
		}
        
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        $truck = new \App\Truck;

        return view('admin/truck_form', [
            'truck' => $truck
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
        $truck=new \App\Truck();
		
		$t=\App\Truck::where('ownerId',$request->ownerId)->first();
		if($t){
			return redirect()->back()->withDanger('A user cannot own more than one truck!');
        
		}
		//var_dump($request->ownerId);
		$user=\App\User::where('id',$request->ownerId)->first();
		
		if($user==null){
			return redirect()->back()->withDanger('User Id is invalid!');
        
		}
		//var_dump($truck->ownerId);
		//exit();
			$truck->ownerId=$user->id;
		$truck->truckName=$request->truckName;
		$truck->save();

        return redirect('truck')->withSuccess('Successfully added!');
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
        $truck = \App\Truck::findOrFail($id);

        return view('admin/truck_form', [
            'truck' => $truck
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
		$truck = \App\Truck::findOrFail($id);
		$t=\App\Truck::where('ownerId',$request->ownerId)->first();
		if($t&&$truck->ownerId!=$request->ownerId){
			return redirect()->back()->withDanger('A user cannot own more than one truck!');
        
		}
		$user=\App\User::where('id',$request->ownerId)->first();
		if(!$user){
			return redirect()->back()->withDanger('User Id is invalid!');
        
		}
        $truck->ownerId=$request->ownerId;
		$truck->truckName=$request->truckName;
		$truck->save();

        return redirect('truck')->withSuccess('Successfully saved!');
 
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $truck = \App\Truck::with('menus')->findOrFail($id);

		$truck->menus()->delete();
		$truck->delete();

        return redirect()->back()->withDanger('Has been deleted!');
    }
}
