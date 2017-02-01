<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Truck extends Model
{
	 /**
     * The database table used by the model.
     *
     * @var string
     */
    protected $table = 'truck';
	
	public function owner(){
		return $this->belongsTo('App\User','ownerId','id');
	}
	
	public function menus(){
		return $this->hasMany('App\Menu','truckId','id');
	}
	
	public function comments(){
		return $this->hasMany('App\Comment','regardingTo','id');
	}
}
