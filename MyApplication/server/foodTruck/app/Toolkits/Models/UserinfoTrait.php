<?php

namespace App\Toolkits\Models;

trait UserinfoTrait
{
    public function getUserNicknameAttribute()
    {
        return $this->user->nickname;
    }

    public function getUserThumbnailAttribute()
    {
        return $this->user->thumbnail;
    }
}
