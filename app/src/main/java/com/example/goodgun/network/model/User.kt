package com.example.goodgun.network.model

class User(
    var u_email: String,
    var u_password: String,
    var u_name: String,
    var u_gender: String,
    var u_height: String,
    var u_weight: String,
    var u_age: String,
    var u_allergy: List<String>,
    var u_exercise_type: String,
    var u_exercise_freq: String,
    var u_physical_goals: String,
) {
    constructor() : this("no_email", "google_account", "no_name", "M", "", "", "", emptyList(), "", "", "no_goal")
    constructor(email: String, password: String, name: String) : this(email, password, name, "M", "", "", "", emptyList(), "", "", "no_goal")
    constructor(email: String, name: String) : this(email, "google_account", name, "M", "", "", "", emptyList(), "", "", "no_goal")
}
