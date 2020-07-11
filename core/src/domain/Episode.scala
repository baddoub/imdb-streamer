package domain

final case class Episode(id: String,
                         parentId: String,
                         seasonNumber: Int,
                         episodeNumber: Int)
