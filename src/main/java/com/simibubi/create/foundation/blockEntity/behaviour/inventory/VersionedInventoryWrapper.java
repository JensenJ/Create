package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.simibubi.create.content.contraptions.Contraption;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class VersionedInventoryWrapper implements Storage<ItemVariant> {

	public static final AtomicInteger idGenerator = new AtomicInteger();

	protected Storage<ItemVariant> inventory;

	private int version;
	private final int id;

	public VersionedInventoryWrapper(Storage<ItemVariant> inventory) {
		this.id = idGenerator.getAndIncrement();
		this.inventory = inventory;
	}

	public void incrementVersion() {
		version++;
	}

	public int getId() {
		return id;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long inserted = inventory.insert(resource, maxAmount, transaction);
		if(inserted != 0){
			incrementVersion();
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = inventory.extract(resource, maxAmount, transaction);
		if (extracted != 0){
			incrementVersion();
		}
		return extracted;
	}

	@Override
	public boolean supportsInsertion() {
		return inventory.supportsInsertion();
	}

	@Override
	public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return inventory.simulateInsert(resource, maxAmount, transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return inventory.supportsExtraction();
	}

	@Override
	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return inventory.simulateExtract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return inventory.iterator();
	}

	@Override
	public @Nullable StorageView<ItemVariant> exactView(ItemVariant resource) {
		return inventory.exactView(resource);
	}

	@Override
	public long getVersion(){
		return version;
	}

}
