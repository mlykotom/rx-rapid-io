package com.futupilot.android.rx.rapid.event;

import android.support.annotation.NonNull;

import java.util.List;

import io.rapid.ListUpdate;
import io.rapid.RapidDocument;


public class RxRapidEvent<T> extends RxRapidBaseEvent {
	@NonNull public final List<RapidDocument<T>> documents;


	public RxRapidEvent(@NonNull List<RapidDocument<T>> documents, @NonNull ListUpdate listUpdate) {
		super(listUpdate);
		this.documents = documents;
	}
}