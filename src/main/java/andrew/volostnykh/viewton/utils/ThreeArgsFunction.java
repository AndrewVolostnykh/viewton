package andrew.volostnykh.viewton.utils;

@FunctionalInterface
public interface ThreeArgsFunction<ARG1, ARG2, ARG3, RETURN> {

    RETURN apply(ARG1 arg1, ARG2 arg2, ARG3 arg3);
}
