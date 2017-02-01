<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

Route::get('/', function () {
    return view('welcome');
	
});
Route::get('user/login', 'UserController@login');
Route::resource('user', 'UserController');
Route::resource('order', 'OrderController');
Route::resource('truck', 'TruckController');
Route::resource('menu', 'MenuController');
