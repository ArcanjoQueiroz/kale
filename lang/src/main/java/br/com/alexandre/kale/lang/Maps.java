package br.com.alexandre.kale.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Map;
import java.util.Optional;

public class Maps {

    @SuppressWarnings("unchecked")
    public static <T>Optional<T> getValue(final String key, final Map<String, Object> map, final Class<T> klass) {
        checkArgument(!isNullOrEmpty(key), "Parâmetro 'key' inválido. Parâmetro vazio.");
        checkArgument(klass != null, "Parâmetro 'klass' inválido. Parâmetro nulo.");
        if (map == null || map.isEmpty()) {
            return Optional.empty();
        }
        final Object value = map.containsKey(key.trim()) ?
                map.get(key.trim()) :
                map.get(key.toLowerCase().trim());
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof String && isNullOrEmpty(value.toString())) {
            return Optional.empty();
        }
        if (klass.isInstance(value)) {
            return Optional.of(klass.cast(value));
        } else if (value instanceof String) {
            if (Boolean.class.isAssignableFrom(klass)) {
                return (Optional<T>) Optional.of(Boolean.parseBoolean(value.toString()));
            }
        }        
        throw new IllegalArgumentException("Incompatible classes: '" + value.getClass().getName() +  "' and '" + klass.getName() + "'");
    }
}
