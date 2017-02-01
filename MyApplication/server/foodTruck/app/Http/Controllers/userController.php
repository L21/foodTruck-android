<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;

class userController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $builder = \App\User::orderBy('id', 'desc');

        $users = $builder->paginate();

        return view('admin/users_list', ['users' => $users]);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        return view("reg_form");
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
		if($request->has('email')){
			$user = \App\User::firstOrCreate(['email'=>$request->email]);
		}else if($request->has('phone')){
			$user = \App\User::firstOrCreate(['phone'=>$request->phone]);
		}else{
			return response()->json([
				'status' => 'wrong format'
			]);
		}
		
        
		if($user->wasRecentlyCreated){
			$user->password=$request->password;
            $user->oneSignalToken=$request->oneSignalToken;
			$user->save();
			return response()->json([
				'status' => 'success'
			]);
		} else {
            return response()->json([
				'status' => 'duplicated email and phone number'
			]);
        }
        //$user = new User(Input::all());
        
    }
	
	public function login(Request $request){
		if($request->isEmail=='true'){
			$users=\App\User::where('email',$request->email)->where('password',$request->password)->with('truck')->get();

        
        
		}else{
			$users=\App\User::where('phone',$request->phone)->where('password',$request->password)->with('truck')->get();

		}



        if($users->isEmpty()){
            // update user's push notification token

			return response()->json([
				'status' => 'User not exists or wrong password'
			]);
		}else{
            foreach ($users as $user){
                if($request->oneSignalToken != $user->oneSignalToken){
                    $user->oneSignalToken = $request->oneSignalToken;
                    $user->save();
                }
            }
			return response()->json([
				'status' => 'success',
                'user' => $user
			]);
		}
		
	}

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        
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
        $user = \App\User::findOrFail($id);
		if($user->truck()!=null){
					$user->truck()->first()->menus()->delete();
		$user->truck()->delete();
		}
        $user->orders()->delete();
		$user->delete();

        return redirect()->back()->withDanger('Has been deleted!');
    }
}
