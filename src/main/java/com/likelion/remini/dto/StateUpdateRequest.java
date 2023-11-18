package com.likelion.remini.dto;

import com.likelion.remini.domain.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StateUpdateRequest {

    private State state;
}
