package pinus.desktop.viewmodel;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChooseResultVM<T> {
    private boolean cancelled;
    private Optional<T> data;
}
