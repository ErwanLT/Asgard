package fr.eletutour.asgard.odin.patterns.behavioral;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectiveObserver<T> implements Observer<T> {
    private final Object target;
    private final String methodName;

    public ReflectiveObserver(Object target, String methodName) {
        if (target == null) {
            throw new NullPointerException("La cible ne peut pas être null");
        }
        if (methodName == null) {
            throw new NullPointerException("Le nom de la méthode ne peut pas être null");
        }
        this.target = target;
        this.methodName = methodName;
    }

    @Override
    public void update(T data) {
        try {
            // Cherche toutes les méthodes avec le nom donné
            Method[] methods = target.getClass().getMethods();
            Method matchingMethod = null;
            
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                    Parameter param = method.getParameters()[0];
                    Class<?> paramType = param.getType();
                    
                    // Vérifie si les types sont compatibles
                    if (paramType.isPrimitive()) {
                        // Pour les types primitifs, vérifie si le type de données peut être converti
                        if ((paramType == int.class && data instanceof Number) ||
                            (paramType == long.class && data instanceof Number) ||
                            (paramType == float.class && data instanceof Number) ||
                            (paramType == double.class && data instanceof Number) ||
                            (paramType == boolean.class && data instanceof Boolean) ||
                            (paramType == char.class && data instanceof Character)) {
                            matchingMethod = method;
                            break;
                        }
                    } else if (paramType.isAssignableFrom(data.getClass())) {
                        // Pour les types objets, vérifie si le type de données est assignable
                        matchingMethod = method;
                        break;
                    }
                }
            }
            
            if (matchingMethod == null) {
                throw new RuntimeException("Aucune méthode compatible trouvée pour " + methodName + " avec le type " + data.getClass().getName());
            }
            
            matchingMethod.invoke(target, data);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de la méthode " + methodName, e);
        }
    }
} 