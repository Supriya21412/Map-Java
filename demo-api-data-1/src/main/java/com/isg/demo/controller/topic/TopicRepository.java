package com.isg.demo.controller.topic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, Long> {

	@Query("Select T from Topic T where T.id = :id")
	Topic findTopic(Long id);
	
	void deleteById(Long id);

	public List<Topic> findAll();

}
