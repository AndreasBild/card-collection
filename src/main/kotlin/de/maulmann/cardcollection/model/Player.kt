package de.maulmann.cardcollection.model
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
@BatchSize(size = 50)
class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    var surname : String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    @JsonIgnore
    var sport: Sport?,
)