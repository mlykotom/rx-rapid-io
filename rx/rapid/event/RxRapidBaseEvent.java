package com.futupilot.android.rx.rapid.event;

import android.support.annotation.NonNull;

import io.rapid.ListUpdate;


public abstract class RxRapidBaseEvent {
	@NonNull public final ListUpdate listUpdate;


	public RxRapidBaseEvent(@NonNull ListUpdate listUpdate) {
		this.listUpdate = listUpdate;
	}
}
