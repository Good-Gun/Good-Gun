package com.example.goodgun

class User(
    var u_email: String,
    var u_password: String,
    var u_name: String,
    var u_height: String,
    var u_weight: String,
    var u_age: String,
    var u_allergy: List<String>,
    var u_exercise_freq: Int,
    var u_physical_goals: String,
) {
    constructor() : this("no_email", "google_account", "no_name", "", "", "", emptyList(), 0, "no_goal")
    constructor(email: String, password: String, name: String) : this(email, password, name, "", "", "", emptyList(), 0, "no_goal")
    constructor(email: String, name: String) : this(email, "google_account", name, "", "", "", emptyList(), 0, "no_goal")
}
