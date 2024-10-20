package com.cortex.backend.chat.api;

import com.cortex.backend.core.domain.ChatRoom;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {
  Optional<ChatRoom> findBySenderIdAndRecipentId(Long senderId, Long recipentId);
}
