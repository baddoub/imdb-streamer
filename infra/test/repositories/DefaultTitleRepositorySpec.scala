package repositories

import domain.Title.TitleType.TvSeries
import domain.{Episode, GreatestTvSeries}

class DefaultTitleRepositorySpec extends BaseRepoSpec {

  describe("DefaultTitleRepositoryRepos") {
    describe("save") {
      it("should save entities") {
        val epSeries1: Seq[Episode] = tenEpisodes.take(3).toList
        val _: Seq[Int] = epSeries1.map(d => db.episodeRepository.save(d)).map(_.unsafeRunSync())
        db.episodeRepository.selectAll.unsafeRunSync() shouldBe epSeries1
      }
    }

    describe("tvSeriesWithGreatestNumberOfEpisodes") {
      it("should return title with greatest number of episodes") {
        val series1 = title.copy(titleType = TvSeries)
        db.titleRepository.save(series1).unsafeRunSync()
        val series2 = title.copy(id = "newId", titleType = TvSeries)
        db.titleRepository.save(series2).unsafeRunSync()

        val expectedNumber = 5
        tenEpisodes
          .take(expectedNumber)
          .toList
          .map(e => db.episodeRepository.save(e.copy(parentId = series1.id)).unsafeRunSync())

        tenEpisodes
          .takeRight(3)
          .toList
          .map(e => db.episodeRepository.save(e.copy(parentId = series2.id)).unsafeRunSync())

        db.titleRepository.tvSeriesWithGreatestNumberOfEpisodes().unsafeRunSync() shouldBe
          GreatestTvSeries(title.id, title.primaryTitle, expectedNumber)

      }
    }
  }
}
