package com.nexis.edgesync.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BenchmarkDao_Impl implements BenchmarkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BenchmarkRunEntity> __insertionAdapterOfBenchmarkRunEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public BenchmarkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBenchmarkRunEntity = new EntityInsertionAdapter<BenchmarkRunEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `benchmark_runs` (`id`,`sessionId`,`timestamp`,`protocol`,`avgLatencyMs`,`reliabilityPercent`,`successCount`,`failureCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BenchmarkRunEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getProtocol());
        statement.bindLong(5, entity.getAvgLatencyMs());
        statement.bindDouble(6, entity.getReliabilityPercent());
        statement.bindLong(7, entity.getSuccessCount());
        statement.bindLong(8, entity.getFailureCount());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM benchmark_runs";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<BenchmarkRunEntity> runs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBenchmarkRunEntity.insert(runs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BenchmarkRunEntity>> observeAll() {
    final String _sql = "SELECT * FROM benchmark_runs ORDER BY sessionId DESC, protocol ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"benchmark_runs"}, new Callable<List<BenchmarkRunEntity>>() {
      @Override
      @NonNull
      public List<BenchmarkRunEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfAvgLatencyMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgLatencyMs");
          final int _cursorIndexOfReliabilityPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "reliabilityPercent");
          final int _cursorIndexOfSuccessCount = CursorUtil.getColumnIndexOrThrow(_cursor, "successCount");
          final int _cursorIndexOfFailureCount = CursorUtil.getColumnIndexOrThrow(_cursor, "failureCount");
          final List<BenchmarkRunEntity> _result = new ArrayList<BenchmarkRunEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BenchmarkRunEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final long _tmpAvgLatencyMs;
            _tmpAvgLatencyMs = _cursor.getLong(_cursorIndexOfAvgLatencyMs);
            final double _tmpReliabilityPercent;
            _tmpReliabilityPercent = _cursor.getDouble(_cursorIndexOfReliabilityPercent);
            final int _tmpSuccessCount;
            _tmpSuccessCount = _cursor.getInt(_cursorIndexOfSuccessCount);
            final int _tmpFailureCount;
            _tmpFailureCount = _cursor.getInt(_cursorIndexOfFailureCount);
            _item = new BenchmarkRunEntity(_tmpId,_tmpSessionId,_tmpTimestamp,_tmpProtocol,_tmpAvgLatencyMs,_tmpReliabilityPercent,_tmpSuccessCount,_tmpFailureCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
