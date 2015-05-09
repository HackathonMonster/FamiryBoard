package com.hackm.famiryboard.model.enumerate;

import com.android.volley.Request;

public enum NetworkTasks {
    GetDeliveryDay(0, Request.Method.GET),
    GetStampCategory(1, Request.Method.GET),
    GetStamp(2, Request.Method.GET),
    PostOrderCake(3, Request.Method.POST),

	PostPicture(4, Request.Method.POST),
	PostLogin(5, Request.Method.POST),
	PostRegistFamiry(6, Request.Method.POST),
	;
	public int id;
	//Request
	public int method;
	
	private NetworkTasks(int id, int method) {
		this.id = id;
		this.method = method;
	}
}
