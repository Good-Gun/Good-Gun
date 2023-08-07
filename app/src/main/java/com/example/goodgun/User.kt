package com.example.goodgun

import java.time.LocalDate

class User ( var u_email:String,
             var u_password:String,
             var u_name:String,
             var u_height:Int,
             var u_weight:Int,
             var u_allergy:String,
             var u_exercise_freq:Int,
             var u_physical_goals:String){

    constructor(email: String, password:String,name:String) : this(email, password, name, 0, 0, "", 0, "")
    constructor(email: String,name:String) : this(email,"google_account", name, 0, 0, "", 0, "")
    constructor():this(
        "no_email","google_account", "no_name", 0, 0, "", 0, ""
    )
}