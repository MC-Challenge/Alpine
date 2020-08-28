package me.zero.alpine.bus;

import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Modified {@code EventHandler}
 * This handler also searches for listeners in the superclass
 *
 * @author Madakai
 * @since 28/08/2020
 */
public class ExtendEventManager extends EventManager {

    @Override
    public void subscribe(Listenable listenable) {
        List<Listener> listeners = SUBSCRIPTION_CACHE.computeIfAbsent(listenable, this::findListener);

        listeners.forEach(this::subscribe);
    }

    /**
     * Scans the class and his superclasses for listeners
     *
     * @param listenable This listenable will be scanned
     * @return A list of all found listeners
     */
    private List<Listener> findListener(final Listenable listenable) {
        final List<Listener> listeners = new ArrayList<>();

        Class<?> clazz = listenable.getClass();

        do {
            if (clazz.getPackage().getName().startsWith("java."))
                break;

            listeners.addAll(Arrays.stream(clazz.getDeclaredFields())
                    .filter(ExtendEventManager::isValidField)
                    .map(field -> asListener(listenable, field))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return listeners;
    }
}
