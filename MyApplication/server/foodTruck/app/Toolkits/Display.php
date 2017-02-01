<?php

namespace App\Toolkits;

use App\Models\Appointment;
use App\Models\Job;
use App\Models\User;

class Display
{
    public static function showDate($datetime)
    {
        $datetime = (string)$datetime;
        $ts = strtotime($datetime);
        $datets = substr($datetime, 0, 10);
        $yearts = substr($datetime, 0, 4);

        $now = time();
        $datenow = date('Y-m-d');
        $yearnow = date('Y');


        if ($now - $ts < 60) {
            $r = 'Just Now';
        } elseif ($now - $ts < 3600) {
            $r = floor(($now - $ts) / 60) . 'mins ago';
        } elseif ($now - $ts < 86400 && $datets == $datenow) {
            $r = substr($datetime, 11, 5);
        } elseif ($now - $ts < 31536000 && $yearts == $yearnow) {
            $r = substr($datetime, 5, 11);
        } else {
            $r = $datetime;
        }

        return "<span title='$datetime'>$r</span>";
    }

    public static function indicator($status)
    {
        switch ($status) {
            case Appointment::STATUS_PENDING:
                return '<span class="glyphicon glyphicon-question-sign text-muted"></span>';
            case Appointment::STATUS_REJECTED:
                return '<span class="glyphicon glyphicon-remove text-danger"></span>';
            case Appointment::STATUS_APPROVED:
                return '<span class="glyphicon glyphicon-ok text-success"></span>';
        }

        return '';
    }

    public static function jobType($type)
    {
        switch ($type) {
            case Job::TYPE_APPLY:
                return '毛遂自荐';
                break;
            case Job::TYPE_RECRUIT:
                return '招兵买马';
                break;
            case Job::TYPE_PARTNERSHIP:
                return '众志成城';
                break;
        }

        return '';
    }

    public static function role($role)
    {
        switch ($role) {
            case User::ROLE_ADMIN:
                return '管理员';
            case User::ROLE_WORKER:
                return '用户';
            case User::ROLE_MASTER:
                return '导师';
        }

        return '';
    }

    public static function ranking($ranking)
    {
        return $ranking ? sprintf('%.1f', $ranking / 2) : '尚未评分';
    }
}
