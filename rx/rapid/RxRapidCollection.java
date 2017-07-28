package com.futupilot.android.rx.rapid;

import android.support.annotation.NonNull;

import com.futupilot.android.rx.rapid.event.RxRapidEvent;
import com.futupilot.android.rx.rapid.event.RxRapidEventSingle;

import java.util.List;

import io.rapid.ListUpdate;
import io.rapid.RapidCallback;
import io.rapid.RapidCollectionReference;
import io.rapid.RapidCollectionSubscription;
import io.rapid.RapidDocument;
import io.rapid.RapidError;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Cancellable;


public final class RxRapidCollection {


	private RxRapidCollection() {}


	/**
	 * Subscribes to all documents from collection which complies to the query.
	 * Flowable DOES NOT COMPLETE
	 *
	 * @param reference rapid.io reference.
	 * @param <T>       type of object being returned
	 * @return flowable with events (contains all documents and updates)
	 */
	@NonNull
	public static <T> Flowable<RxRapidEvent<T>> subscribeWithListUpdates(@NonNull final RapidCollectionReference<T> reference) {
		return Flowable.create(new FlowableOnSubscribe<RxRapidEvent<T>>() {
			@Override
			public void subscribe(@NonNull final FlowableEmitter<RxRapidEvent<T>> emitter) throws Exception {
				final RapidCollectionSubscription subscription = reference
						.subscribeWithListUpdates(new RapidCallback.CollectionUpdates<T>() {
							@Override
							public void onValueChanged(List<RapidDocument<T>> rapidDocuments, @NonNull ListUpdate listUpdate) {
								emitter.onNext(new RxRapidEvent<T>(rapidDocuments, listUpdate));
							}
						})
						.onError(new RapidCallback.Error() {
							@Override
							public void onError(RapidError error) {
								emitter.onError(error);
							}
						});

				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() throws Exception {
						subscription.unsubscribe();
					}
				});
			}
		}, BackpressureStrategy.BUFFER);
	}


	/**
	 * Subscribes to all documents from collection which complies to the query.
	 * Flowable DOES NOT COMPLETE
	 *
	 * @param reference rapid.io reference.
	 * @param <T>       type of object being returned
	 * @return flowable with all documents
	 */
	@NonNull
	public static <T> Flowable<RapidDocument<T>> subscribe(@NonNull final RapidCollectionReference<T> reference) {
		return Flowable.create(new FlowableOnSubscribe<RapidDocument<T>>() {
			@Override
			public void subscribe(@NonNull final FlowableEmitter<RapidDocument<T>> emitter) throws Exception {
				final RapidCollectionSubscription subscription = reference
						.subscribe(new RapidCallback.Collection<T>() {
							@Override
							public void onValueChanged(@NonNull List<RapidDocument<T>> rapidDocuments) {
								for(RapidDocument<T> rapidDocument : rapidDocuments) {
									emitter.onNext(rapidDocument);
								}
							}
						})
						.onError(new RapidCallback.Error() {
							@Override
							public void onError(RapidError error) {
								emitter.onError(error);
							}
						});

				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() throws Exception {
						subscription.unsubscribe();
					}
				});
			}
		}, BackpressureStrategy.BUFFER);
	}


	/**
	 * Fetches all documents from collection which complies to the query.
	 * Flowable COMPLETES after all documents sent.
	 *
	 * @param reference rapid.io reference.
	 * @param <T>       type of object being returned
	 * @return flowable with all documents
	 */
	@NonNull
	public static <T> Flowable<RapidDocument<T>> fetch(@NonNull final RapidCollectionReference<T> reference) {
		return Flowable.create(new FlowableOnSubscribe<RapidDocument<T>>() {
			@Override
			public void subscribe(@NonNull final FlowableEmitter<RapidDocument<T>> emitter) throws Exception {
				final RapidCollectionSubscription subscription = reference
						.fetch(new RapidCallback.Collection<T>() {
							@Override
							public void onValueChanged(@NonNull List<RapidDocument<T>> rapidDocuments) {
								for(RapidDocument<T> rapidDocument : rapidDocuments) {
									emitter.onNext(rapidDocument);
								}
								emitter.onComplete();
							}
						})
						.onError(new RapidCallback.Error() {
							@Override
							public void onError(RapidError error) {
								emitter.onError(error);
							}
						});

				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() throws Exception {
						subscription.unsubscribe();
					}
				});
			}
		}, BackpressureStrategy.BUFFER);
	}


	/**
	 * Fetches first document from collection.
	 * Fails with {@link RxRapidException.Type#ERROR_NOT_FOUND} when no item found in collection.
	 *
	 * @param reference rapid.io reference. {@link RapidCollectionReference#first()} is appended to the reference call
	 * @param <T>       type of object being returned
	 * @return single with document
	 */
	@NonNull
	public static <T> Single<RapidDocument<T>> fetchFirst(@NonNull final RapidCollectionReference<T> reference) {
		return Single.create(new SingleOnSubscribe<RapidDocument<T>>() {
			@Override
			public void subscribe(@NonNull final SingleEmitter<RapidDocument<T>> emitter) throws Exception {
				final RapidCollectionSubscription subscription = reference
						.first()
						.fetch(new RapidCallback.Collection<T>() {
							@Override
							public void onValueChanged(@NonNull List<RapidDocument<T>> rapidDocuments) {
								if(!rapidDocuments.isEmpty()) {
									emitter.onSuccess(rapidDocuments.get(0));
								} else {
									emitter.onError(new RxRapidException(RxRapidException.Type.ERROR_NOT_FOUND));
								}
							}
						})
						.onError(new RapidCallback.Error() {
							@Override
							public void onError(RapidError error) {
								emitter.onError(error);
							}
						});

				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() throws Exception {
						subscription.unsubscribe();
					}
				});
			}
		});
	}


	/**
	 * Subscribes to collection and observes only first document.
	 * May sent event with null document, which represents removed document.
	 *
	 * @param reference rapid.io reference. {@link RapidCollectionReference#first()} is appended to the reference call
	 * @param <T>       type of object being returned
	 * @return flowable which does not complete
	 */
	@NonNull
	public static <T> Flowable<RxRapidEventSingle<T>> subscribeFirst(@NonNull final RapidCollectionReference<T> reference) {
		return Flowable.create(new FlowableOnSubscribe<RxRapidEventSingle<T>>() {
			@Override
			public void subscribe(@NonNull final FlowableEmitter<RxRapidEventSingle<T>> emitter) throws Exception {
				final RapidCollectionSubscription subscription = reference
						.first()
						.subscribeWithListUpdates(new RapidCallback.CollectionUpdates<T>() {
							@Override
							public void onValueChanged(@NonNull List<RapidDocument<T>> rapidDocuments, @NonNull ListUpdate listUpdate) {
								if(!rapidDocuments.isEmpty()) {
									emitter.onNext(new RxRapidEventSingle<T>(rapidDocuments.get(0), listUpdate));
								} else {
									emitter.onNext(new RxRapidEventSingle<T>(null, listUpdate));
								}
							}
						})
						.onError(new RapidCallback.Error() {
							@Override
							public void onError(RapidError error) {
								emitter.onError(error);
							}
						});


				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() throws Exception {
						subscription.unsubscribe();
					}
				});
			}
		}, BackpressureStrategy.LATEST);
	}
}