package kr.co.hanipactor.application.manager.model;

import java.util.List;

public record PageResponse<T> (List<T> content) {}
