<?php

namespace App\Toolkits\Models;

trait ThumbnailTrait
{
    public function getThumbnailAttribute($value)
    {
        if ($value) {
            return request()->getSchemeAndHttpHost() . '/files/' . $value;
        }

        return $value;
    }
}
