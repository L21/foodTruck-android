<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Menu extends Model
{
	/**
     * The database table used by the model.
     *
     * @var string
     */
    protected $table = 'menu';
    public function truck(){
		return $this->belongsTo('App\Truck','truckId','id');
	}
	
	public function comments(){
		return $this->hasMany('App\Comment','regardingTo','id');
	}
}
