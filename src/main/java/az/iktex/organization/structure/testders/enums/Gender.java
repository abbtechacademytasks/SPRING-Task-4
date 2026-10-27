package az.iktex.organization.structure.testders.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum Gender {
    MALE(1),
    FEMALE(2);

    private final Integer dbCode;

    Gender(Integer dbCode) {
        this.dbCode = dbCode;
    }

    public static Gender findByDbCode(Integer dbCode) {
        return Arrays.stream(Gender.values())
                .filter(gender -> Objects.equals(gender.getDbCode(), dbCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Gender id: " + dbCode));
    }
}
