package io.github.kydzombie.stapitech.api.energy;

import net.minecraft.nbt.NbtCompound;

/**
 * 10 units of energy = 1 tick of fuel.
 */
public interface HasEnergy {
    int getMaxEnergy();
    int getEnergy();

    /**
     * @return How much extra space for energy there is.
     */
    default int getEnergyRemainingSpace() {
        return getMaxEnergy() - getEnergy();
    }
    void setEnergy(int amount);

    /**
     * @param amount The amount of energy you are requesting.
     * @return The amount of energy that was actually consumed.
     */
    default int consumeEnergy(int amount) {
        var consumed = Math.min(getEnergy(), amount);
        setEnergy(getEnergy() - consumed);
        return consumed;
    }

    /**
     * @param amount The amount of energy you are sending.
     * @return The amount of energy that was actually sent.
     */
    default int chargeEnergy(int amount) {
        var charged = Math.min(getEnergyRemainingSpace(), amount);
        setEnergy(getEnergy() + charged);
        return charged;
    }

    /**
     * @param target What to send the energy to.
     * @param amount The amount of energy you are trying to send.
     * @return The amount of energy that was actually sent.
     */
    default int sendEnergyTo(HasEnergy target, int amount) {
        var sent = Math.min(Math.min(target.getEnergyRemainingSpace(), amount), this.getEnergy());
        this.setEnergy(this.getEnergy() - sent);
        target.setEnergy(target.getEnergy() + sent);
        return sent;
    }

    default void readEnergyNbt(NbtCompound nbt) {
        setEnergy(nbt.getInt("stapitech:energy"));
    }

    default void writeEnergyNbt(NbtCompound nbt) {
        nbt.putInt("stapitech:energy", getEnergy());
    }
}
