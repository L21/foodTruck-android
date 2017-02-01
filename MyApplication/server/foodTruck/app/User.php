<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    /**
     * The database table used by the model.
     *
     * @var string
     */
    protected $table = 'user';

    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = ['name', 'email', 'phone','password', 'oneSignalToken'];
	
	public function orders(){
		return $this->hasMany('App\Order','userId','id');
	}
	public function truck(){
		return $this->hasOne('App\Truck','ownerId','id');
	}
	
	public function comments(){
		return $this->hasMany('App\Comment','regardingTo','id');
	}
}
