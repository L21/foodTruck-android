<?php

namespace App\Toolkits\Models;

trait FavoriteableTrait
{
    public function getIsFavoriteAttribute()
    {
        if (!\Auth::check()) {
            return false;
        }

        if ($this->favorites()->where('user_id', \Auth::user()->id)->first()) {
            return true;
        } else {
            return false;
        }
    }

    public function getFavoriteCountAttribute()
    {
        return $this->favorites()->count();
    }
}
