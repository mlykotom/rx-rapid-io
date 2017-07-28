package com.futupilot.android.rx.rapid.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.rapid.ListUpdate;
import io.rapid.RapidDocument;


public class RxRapidEventSingle<T> extends RxRapidBaseEvent {
	@Nullable public final RapidDocument<T> document;


	/**
	 * Event representing one document from rapid and updates
	 *
	 * @param document   may be null which represents deleted object
	 * @param listUpdate update from rapid
	 */
	public RxRapidEventSingle(@Nullable RapidDocument<T> document, @NonNull ListUpdate listUpdate) {
		super(listUpdate);
		this.document = document;
	}
}
